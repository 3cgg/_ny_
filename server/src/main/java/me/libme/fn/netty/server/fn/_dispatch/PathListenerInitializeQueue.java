package me.libme.fn.netty.server.fn._dispatch;

import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by J on 2018/1/30.
 */
public class PathListenerInitializeQueue {

    private final Executor executor= Executors.newFixedThreadPool(1);

    private final Queue<Runnable> queue=new LinkedBlockingQueue<>();

    private static final PathListenerInitializeQueue instance=new PathListenerInitializeQueue();

    private PathListenerInitializeQueue() {
    }

    public static PathListenerInitializeQueue get(){return instance;}


    public void offer(Runnable runnable){
        queue.offer(runnable);
    }


    public void allInitialize(){
        Runnable current;
        while ((current=queue.poll())!=null){
            executor.execute(current);
        }
    }


}
