package me.libme.fn.netty.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by J on 2017/9/7.
 */
public class SimpleRequestHandler{

    private static final Logger logger= LoggerFactory.getLogger(SimpleRequestHandler.class);


    public static QueueHolder queueHolder=new ArrayBlockingQueueHolder();


    public void handle(final HttpRequest httpRequest) {
        queueHolder.queue().offer(httpRequest);

    }


}
