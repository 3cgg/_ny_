package me.libme.fn.netty.server;

import me.libme.kernel._c.json.JJSON;
import me.libme.xstream.*;
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

    private final RequestMappingHandler requestMappingHandler;

    public RequestConsumer(RequestMappingHandler requestMappingHandler) {
        this.requestMappingHandler = requestMappingHandler;
    }

    @Override
    protected void prepare(Tupe tupe) {
        super.prepare(tupe);
    }

    @Override
    protected void doConsume(Tupe tupe,TupeContext tupeContext) throws Exception {
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
