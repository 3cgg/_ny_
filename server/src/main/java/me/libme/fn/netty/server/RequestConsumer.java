package me.libme.fn.netty.server;

import me.libme.kernel._c.json.JJSON;
import me.libme.xstream.Compositer;
import me.libme.xstream.ConsumerMeta;
import me.libme.xstream.EntryTuple;
import me.libme.xstream.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.Iterator;

/**
 * Created by J on 2018/1/18.
 */
public class RequestConsumer extends Compositer {

    private static final Logger logger= LoggerFactory.getLogger(RequestConsumer.class);

    private final RequestMappingHandler requestMappingHandler;

    public RequestConsumer(ConsumerMeta consumerMeta,RequestMappingHandler requestMappingHandler) {
        super(consumerMeta);
        this.requestMappingHandler = requestMappingHandler;
    }

    @Override
    protected void prepare(Tuple tuple) {
        super.prepare(tuple);
    }

    @Override
    protected void doConsume(Tuple tuple) throws Exception {
        HttpRequest httpRequest=null;
        Iterator<EntryTuple.Entry> iterator=tuple.iterator();
        if (iterator.hasNext()) {
            httpRequest=(HttpRequest) iterator.next().getValue();
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
