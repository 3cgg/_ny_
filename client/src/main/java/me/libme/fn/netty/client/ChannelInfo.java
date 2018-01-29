package me.libme.fn.netty.client;

/**
 * Created by J on 2018/1/29.
 */
public interface ChannelInfo {

    String uri();

    boolean ssl();

    String host();

    int port();

}
