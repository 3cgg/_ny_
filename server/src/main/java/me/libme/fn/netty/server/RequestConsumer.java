package me.libme.fn.netty.server;

import me.libme.kernel._c.json.JJSON;
import me.libme.xstream.Compositer;
import me.libme.xstream.ConsumerMeta;
import me.libme.xstream.FlexTupe;
import me.libme.xstream.Tupe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

/**
 * Created by J on 2018/1/18.
 */
public class RequestConsumer extends Compositer {

    private static final Logger logger= LoggerFactory.getLogger(RequestConsumer.class);

    {
        consumerMeta = new ConsumerMeta();
        consumerMeta.setName("Internal Request Dispatcher");
    }

    private FlexTupe flexTupe;

    private FlexTupe markerTupe;

    private FlexTupe exceptionTupe;

    private final RequestMappingHandler requestMappingHandler;

    public RequestConsumer(RequestMappingHandler requestMappingHandler) {
        this.requestMappingHandler = requestMappingHandler;
    }

    @Override
    protected void prepare(Tupe tupe) {
        super.prepare(tupe);
        flexTupe = new FlexTupe();
        markerTupe = new FlexTupe();
        exceptionTupe = new FlexTupe();
    }

    @Override
    protected void complete(Tupe tupe) {

    }

    @Override
    protected void _finally(Tupe tupe) {

    }

    @Override
    protected FlexTupe flexTupe() {
        return flexTupe;
    }

    @Override
    protected FlexTupe markerTupe() {
        return markerTupe;
    }

    @Override
    protected FlexTupe exceptionTupe() {
        return exceptionTupe;
    }

    @Override
    protected void doConsume(Tupe tupe) throws Exception {
        HttpRequest httpRequest=null;
        while (tupe.hasNext()) {
            httpRequest=(HttpRequest) tupe.next();
        }
        try{
            Object result=requestMappingHandler.handle(httpRequest);
            if(result!=null){
                HttpResponse httpResponse=httpRequest.getHttpResponse();
                httpResponse.write(JJSON.get().format(result).getBytes(Charset.forName("utf-8")));
            }else{
                HttpResponse httpResponse=httpRequest.getHttpResponse();
                httpResponse.write("OK".getBytes(Charset.forName("utf-8")));
            }
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            httpRequest.getHttpResponse().write("sever error.".getBytes(Charset.forName("utf-8")));
        }

    }
}
