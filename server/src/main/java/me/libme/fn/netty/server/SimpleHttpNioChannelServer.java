package me.libme.fn.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import me.libme.xstream.ConsumerMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class SimpleHttpNioChannelServer implements Closeable {

	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleHttpNioChannelServer.class);
	
	private final ServerConfig serverConfig;
	
	private final boolean useSSL;
	
	private EventLoopGroup bossGroup;
	
	private EventLoopGroup workerGroup;

	private AtomicInteger workerThreadIndicator=new AtomicInteger(0);

	private AtomicInteger bossThreadIndicator=new AtomicInteger(0);

	private final RequestMappingHandler requestMappingHandler;

	private RequestProcessor requestProcessor;

	private ScheduledExecutorService windowExecutor;

	private ExecutorService executor;

	/**
	 * !important / micro-batch
	 * @param windowExecutor
	 * @return
	 */
	public SimpleHttpNioChannelServer windowExecutor(ScheduledExecutorService windowExecutor) {
		this.windowExecutor = windowExecutor;
		return this;
	}

	/**
	 * !important / executing thread pool
	 * @param executor
	 * @return
	 */
	public SimpleHttpNioChannelServer executor(ExecutorService executor) {
		this.executor = executor;
		return this;
	}

	public SimpleHttpNioChannelServer(ServerConfig serverConfig,boolean useSSL,RequestMappingHandler requestMappingHandler) {
		this.serverConfig = serverConfig;
		this.useSSL = useSSL;
		this.requestMappingHandler=requestMappingHandler;
	}
	
	public SimpleHttpNioChannelServer(ServerConfig serverConfig,RequestMappingHandler requestMappingHandler) {
		this(serverConfig,false,requestMappingHandler);
	}
	
	public void start() throws Exception{

		Objects.requireNonNull(windowExecutor);
		Objects.requireNonNull(executor);

		 // Configure SSL.
        final SslContext sslCtx;
        if (useSSL) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        } else {
            sslCtx = null;
        }

        // Configure the server.
        EventLoopGroup bossGroup = new NioEventLoopGroup(serverConfig.getLoopThread(),new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				return new Thread(r,"netty-server-acceptor-io-port-"+serverConfig.getPort()+"-"+bossThreadIndicator.incrementAndGet());
			}
		});
        EventLoopGroup workerGroup = new NioEventLoopGroup(serverConfig.getWorkerThread(),new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				return new Thread(r,"netty-server-worker-io-port-"+serverConfig.getPort()+"-"+workerThreadIndicator.incrementAndGet());
			}
		});
        try {

            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
					.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
             .handler(new LoggingHandler(LogLevel.DEBUG))
             .childHandler(new SimpleHttpServerInitializer(sslCtx))
             .bind(serverConfig.getHost(),serverConfig.getPort());
            this.bossGroup=bossGroup;
            this.workerGroup=workerGroup;
			LOGGER.info("Open your web browser and navigate to " +
                    (useSSL? "https" : "http") + "://"+serverConfig.getHost()+":" + serverConfig.getPort() + '/');

			requestProcessor=RequestProcessor.builder()
					.setCount(serverConfig.getWindowCount())
					.setTime(serverConfig.getWindowTime())
					.windowExecutor(windowExecutor)
					.executor(executor)
					.setQueueHolder(SimpleRequestHandler.queueHolder)
					.addConsumerProider(()->new RequestConsumer(new ConsumerMeta("Internal Request Dispatcher"),requestMappingHandler))
					.build();
			requestProcessor.start();

        } catch (Exception e){
        	LOGGER.error(e.getMessage(), e);
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            throw new RuntimeException(e);
        }
	}

	@Override
	public void close() throws IOException {
		bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
		requestProcessor.shutdown();
		windowExecutor.shutdown();
		executor.shutdown();
	}
	
}
