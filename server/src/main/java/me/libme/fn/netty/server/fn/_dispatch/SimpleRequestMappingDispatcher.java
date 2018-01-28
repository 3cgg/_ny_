package me.libme.fn.netty.server.fn._dispatch;

import me.libme.fn.netty.server.HttpRequest;
import me.libme.fn.netty.server.RequestMappingHandler;
import me.libme.kernel._c.cache.JCacheService;
import me.libme.kernel._c.cache.JMapCacheService;
import me.libme.kernel._c.util.Assert;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Created by J on 2018/1/27.
 */
public class SimpleRequestMappingDispatcher implements RequestMappingHandler {

    private JCacheService<String,PathListener> pathListeners=new JMapCacheService<>();

    @Override
    public Object handle(HttpRequest httpRequest) throws Exception {

        String path=httpRequest.getPath();
        PathListener pathListener=pathListeners.get(path);
        if(pathListener==null){
            return "No Mapping found on "+path;
        }
        return pathListener.listen(httpRequest);
    }


    private void validate(String path,PathListener pathListener){

        Method[] methods= pathListener.getClass().getDeclaredMethods();
        int count=0;
        for(Method method:methods){
            if(Modifier.isPublic(method.getModifiers())){
                count++;
            }
        }
        Assert.state(count==1,pathListener.getClass()+" must declare only one public method.");
    }

    public SimpleRequestMappingDispatcher register(String path, PathListener pathListener){
        if(pathListeners.contains(path)){
            throw new IllegalStateException(path+" exists, change to different one.");
        }
        validate(path, pathListener);
        pathListeners.put(path,pathListener);
        return this;
    }

    public SimpleRequestMappingDispatcher replace(String path, PathListener pathListener){
        validate(path, pathListener);
        pathListeners.put(path,pathListener);
        return this;
    }


}
