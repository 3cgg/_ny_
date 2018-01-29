package me.libme.fn.netty.client;

import java.io.IOException;

/**
 * Created by J on 2018/1/29.
 */
public abstract class DynamicChannelExecutor implements ChannelExecutor<NioChannelRunnable> {

    private NioChannelExecutor nioChannelExecutor;

    @Override
    public <V> CallPromise<V> execute(NioChannelRunnable channelRunnable) throws Exception {
        return nioChannelExecutor.execute(channelRunnable);
    }

    boolean ssl() {
        SimpleHttpClientInitializer simpleHttpClientInitializer = (SimpleHttpClientInitializer) nioChannelExecutor.getClientInitializer();
        return simpleHttpClientInitializer.ssl();
    }

    public String uri() {
        return (ssl() ? "https" : "http") + "://" + nioChannelExecutor.getHost() + ":" + nioChannelExecutor.getPort();
    }

    @Override
    public void close() throws IOException {
        nioChannelExecutor.close();
    }

}
