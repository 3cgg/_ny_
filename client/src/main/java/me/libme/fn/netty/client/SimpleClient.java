package me.libme.fn.netty.client;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import me.libme.fn.netty.client.msg.FormMsgBody;
import me.libme.fn.netty.client.msg.IResponse;
import me.libme.fn.netty.msg.BodyDecoder;
import me.libme.kernel._c.util.JClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * Created by J on 2018/1/29.
 */
public class SimpleClient implements InvocationHandler {

    private static final Logger LOGGER= LoggerFactory.getLogger(SimpleClient.class);

    private final ClientChannelExecutor<NioChannelRunnable> channelExecutor;

    private final PathGenerator pathGenerator;

    private final BodyDecoderProvider bodyDecoderProvider;

    private final static _JSON JSON=new _JSON();

    public SimpleClient(ClientChannelExecutor<NioChannelRunnable> channelExecutor, PathGenerator pathGenerator, BodyDecoderProvider bodyDecoderProvider) {
        this.channelExecutor = channelExecutor;
        this.pathGenerator = pathGenerator;
        this.bodyDecoderProvider = bodyDecoderProvider;
    }

    public SimpleClient(ClientChannelExecutor<NioChannelRunnable> channelExecutor, String path, BodyDecoderProvider bodyDecoderProvider) {
        this(channelExecutor, new DirectPath(path),bodyDecoderProvider);
    }

    public SimpleClient(ClientChannelExecutor<NioChannelRunnable> channelExecutor, String path) {
        this(channelExecutor,path,JSON);
    }

    public SimpleClient(ClientChannelExecutor<NioChannelRunnable> channelExecutor, PathGenerator pathGenerator) {
        this(channelExecutor,pathGenerator,JSON);
    }

    private FormMsgBody append(Class<?> _clazz,Object object,FormMsgBody formMsgBody){
        Class<?> clazz=_clazz;
        while(clazz!=null){
            Field[] fields=clazz.getDeclaredFields();
            if(fields.length>0){
                for(Field field:fields){
                    try {
                        formMsgBody.addEntry(field.getName(),
                                JClassUtils.findGetterMethod(_clazz,field.getName()).invoke(object));
                    } catch (Exception e) {
                        throw new IllegalArgumentException(field.getName());
                    }
                }
            }
            clazz=clazz.getSuperclass();
        }
        return formMsgBody;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if(!method.getDeclaringClass().isInterface()) return null;  // we only send request to server when calling method of an interface

        SimpleRequest simpleRequest = SimpleRequest.post();
        simpleRequest.setUrl(channelExecutor.uri() + pathGenerator.path(method, args));
        FormMsgBody formMsgBody = new FormMsgBody();
        Parameter[] parameters=method.getParameters();
        for(int i=0;i<parameters.length;i++){
            Parameter parameter=parameters[i];
            Object object=args[i];
            if(object!=null) { // skip null parameter
                if (JClassUtils.isSimpleType(object.getClass())) {
                    formMsgBody.addEntry(parameter.getName(), object);
                } else {
                    append(object.getClass(), object, formMsgBody);
                }
            }
        }
        simpleRequest.setMessageBody(formMsgBody);
        simpleRequest.addHeader(HttpHeaderNames.CONNECTION.toString(), HttpHeaderValues.KEEP_ALIVE.toString())
                .addHeader(HttpHeaderNames.CONTENT_TYPE.toString(), HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .addHeader(HttpHeaderNames.ACCEPT_ENCODING.toString(), HttpHeaderValues.GZIP.toString());
        try {
            CallPromise callPromise = channelExecutor.execute(new NioChannelRunnable(simpleRequest));
            IResponse response = (IResponse) callPromise.get();
            Object result = response.decode(bodyDecoderProvider.provide(proxy, method, args));
            return result;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(),e);
        }
        return null;
    }


    public interface BodyDecoderProvider{
        BodyDecoder provide(Object proxy, Method method, Object[] args);
    }


    private static class _JSON implements BodyDecoderProvider{

        @Override
        public BodyDecoder provide(Object proxy, Method method, Object[] args) {
            return new BodyDecoder.SimpleJSONDecoder(method.getReturnType());
        }


    }


    public static interface PathGenerator{

        String path(Method method, Object[] args);

    }

    private static class DirectPath implements PathGenerator{
        private final String path;

        public DirectPath(String path) {
            this.path = path;
        }

        @Override
        public String path(Method method, Object[] args) {
            return path;
        }
    }

}
