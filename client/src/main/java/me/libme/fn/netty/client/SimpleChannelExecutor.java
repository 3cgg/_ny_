package me.libme.fn.netty.client;

import java.io.IOException;

public class SimpleChannelExecutor implements ChannelExecutor<NioChannelRunnable> ,ClientChannelExecutor<NioChannelRunnable>{

	private NioChannelExecutor nioChannelExecutor;

	public SimpleChannelExecutor(String host,int port) {
		SimpleHttpClientInitializer httpClientInitializer=ChannelExecutors.newSimpleHttpClientInitializer(false);
		httpClientInitializer.addChannelHandler(new SimpleHttpClientHandler());
		this.nioChannelExecutor=ChannelExecutors.newNioChannelExecutor(host, port,httpClientInitializer);
	}
	
	@Override
	public <V> CallPromise<V> execute(NioChannelRunnable channelRunnable) throws Exception {
		return nioChannelExecutor.execute(channelRunnable);
	}
	
	public boolean ssl(){
		SimpleHttpClientInitializer simpleHttpClientInitializer=(SimpleHttpClientInitializer)nioChannelExecutor.getClientInitializer();
    	return simpleHttpClientInitializer.ssl();
    }
	
	public String uri(){
		return (ssl()?"https":"http")+"://"+nioChannelExecutor.getHost()+":"+nioChannelExecutor.getPort();
	}

	@Override
	public void close() throws IOException {
		nioChannelExecutor.close();
	}

	@Override
	public String host() {
		return nioChannelExecutor.getHost();
	}

	@Override
	public int port() {
		return nioChannelExecutor.getPort();
	}
}
