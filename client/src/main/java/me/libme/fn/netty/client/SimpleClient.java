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

    private final String path;

    private final BodyDecoder bodyDecoder;


    public SimpleClient(ClientChannelExecutor<NioChannelRunnable> channelExecutor, String path, BodyDecoder bodyDecoder) {
        this.channelExecutor = channelExecutor;
        this.path = path;
        this.bodyDecoder = bodyDecoder;
    }

    public SimpleClient(ClientChannelExecutor<NioChannelRunnable> channelExecutor, String path) {
        this(channelExecutor,path,new BodyDecoder.SimpleJSONDecoder(String.class));
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

        SimpleRequest simpleRequest = SimpleRequest.post();
        simpleRequest.setUrl(channelExecutor.uri() + path);
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
            Object result = response.decode(bodyDecoder);
            return result;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(),e);
        }
        return null;
    }


}
