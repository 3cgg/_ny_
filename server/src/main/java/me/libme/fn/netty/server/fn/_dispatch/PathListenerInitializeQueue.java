package me.libme.fn.netty.server.fn._dispatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by J on 2018/1/30.
 */
public class PathListenerInitializeQueue {


    private static final Logger LOGGER = LoggerFactory.getLogger(PathListenerInitializeQueue.class);

//    private final ExecutorService executor= Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private final Queue<Runnable> queue = new LinkedBlockingQueue<>();

    private static final PathListenerInitializeQueue instance = new PathListenerInitializeQueue();

    private PathListenerInitializeQueue() {
    }

    public static PathListenerInitializeQueue get() {
        return instance;
    }


    public void offer(Runnable runnable) {
        queue.offer(runnable);
    }


    public void allInitialize() {
        Runnable current;
        while ((current = queue.poll()) != null) {
            try {
                current.run();
            } catch (Throwable e) {
                LOGGER.error(e.getMessage(), e);
                System.exit(-1);
            }
        }
    }


}
