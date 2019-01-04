package test.me.libme.fn.netty.server;

import me.libme.fn.netty.server.HttpRequest;
import me.libme.fn.netty.server.ServerConfig;
import me.libme.fn.netty.server.SimpleHttpNioChannelServer;
import me.libme.fn.netty.server.fn._dispatch.PathListener;
import me.libme.fn.netty.server.fn._dispatch.SimpleRequestMappingDispatcher;
import me.libme.kernel._c.json.JJSON;
import me.libme.kernel._c.util.JDateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

/**
 * Created by J on 2018/1/27.
 */
public class TestServer {



    private static final Logger LOGGER= LoggerFactory.getLogger(TestServer.class);


    private static ScheduledExecutorService windowExecutor= Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors(), new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r,"window-topology-scheduler");
        }
    });

    private static ExecutorService executor=Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), r->new Thread(r,"real thread on executing topology"));


    public static void main(String[] args) {

        ServerConfig serverConfig=new ServerConfig();
        serverConfig.setHost("127.0.0.1");
        serverConfig.setPort(10089);
        serverConfig.setLoopThread(1);
        serverConfig.setWorkerThread(Runtime.getRuntime().availableProcessors());

        SimpleRequestMappingDispatcher dispatcher=new SimpleRequestMappingDispatcher();

        dispatcher.register("/demo/_test4netty_/name", new PathListener() {
            public String name(String name){
                return name;
            }
        }).register("/demo/info", new PathListener() {
            public String info(String name, int age,HttpRequest httpRequest){
                httpRequest.paramNames().forEach(key-> LOGGER.info(key));
                return name+"-"+age;
            }
        }).register("/demo/map", new PathListener() {
            public Map<String,Object> mapInfo(String name,int age,HttpRequest httpRequest){
                Map<String,Object> map=new HashMap<>();
                map.put("name",name);
                map.put("age",age);
                map.put("time", JDateUtils.formatWithSeconds(new Date()));
                map.put("server", true);
                return map;
            }
        });


        // START SERVER
        SimpleHttpNioChannelServer channelServer =
                new SimpleHttpNioChannelServer(serverConfig,dispatcher);
        try {
            channelServer.start();
            LOGGER.info("Host ["+serverConfig.getHost()+"] listen on port : "+serverConfig.getPort()+", params : "+ JJSON.get().format(serverConfig));
        } catch (Exception e) {
            LOGGER.error( e.getMessage()+", params : "+ JJSON.get().format(serverConfig),e);
            try {
                channelServer.close();
            } catch (IOException e1) {
                LOGGER.error( e1.getMessage()+", params : "+ JJSON.get().format(serverConfig),e1);
                throw new RuntimeException(e);
            }
            throw new RuntimeException(e);
        }


    }
}
