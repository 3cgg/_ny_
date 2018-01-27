package me.libme.fn.netty.server;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by J on 2018/1/20.
 */
public class ArrayBlockingQueueHolder implements QueueHolder {

    private ArrayBlockingQueue<HttpRequest> queue=new ArrayBlockingQueue<>(100000);


    @Override
    public Queue<HttpRequest> queue() {
        return queue;
    }
}
