package me.libme.fn.netty.client;

import java.io.IOException;

/**
 * Created by J on 2018/1/30.
 */
public class DynamicClientChannelExecutor implements ClientChannelExecutor<NioChannelRunnable> {


    private final SimpleChannelExecutorProvider simpleChannelExecutorProvider;

    public DynamicClientChannelExecutor(SimpleChannelExecutorProvider simpleChannelExecutorProvider) {
        this.simpleChannelExecutorProvider=simpleChannelExecutorProvider;
    }

    @Override
    public <V> CallPromise<V> execute(NioChannelRunnable nioChannelRunnable) throws Exception {
        return simpleChannelExecutorProvider.provider().execute(nioChannelRunnable);
    }

    @Override
    public void close() throws IOException {

        simpleChannelExecutorProvider.provider().close();
    }

    @Override
    public String uri() {
        return simpleChannelExecutorProvider.provider().uri();
    }

    @Override
    public boolean ssl() {
        return simpleChannelExecutorProvider.provider().ssl();
    }

    @Override
    public String host() {
        return simpleChannelExecutorProvider.provider().host();
    }

    @Override
    public int port() {
        return simpleChannelExecutorProvider.provider().port();
    }

    public interface SimpleChannelExecutorProvider{
        SimpleChannelExecutor provider();
    }






}
