package me.libme.fn.netty.server;

import java.util.Queue;

/**
 * Created by J on 2018/1/20.
 */
public interface QueueHolder {

    Queue<HttpRequest> queue();

}
