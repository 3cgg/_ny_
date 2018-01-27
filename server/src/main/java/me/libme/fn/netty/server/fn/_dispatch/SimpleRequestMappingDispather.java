package me.libme.fn.netty.server.fn._dispatch;

import me.libme.fn.netty.server.HttpRequest;
import me.libme.fn.netty.server.RequestMappingHandler;
import me.libme.kernel._c.cache.JCacheService;
import me.libme.kernel._c.cache.JMapCacheService;

/**
 * Created by J on 2018/1/27.
 */
public class SimpleRequestMappingDispather implements RequestMappingHandler {

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

    public SimpleRequestMappingDispather register(String path,PathListener pathListener){
        if(pathListeners.contains(path)){
            throw new IllegalStateException(path+" exists, change to different one.");
        }
        pathListeners.put(path,pathListener);
        return this;
    }

    public SimpleRequestMappingDispather replace(String path,PathListener pathListener){
        pathListeners.put(path,pathListener);
        return this;
    }


}
