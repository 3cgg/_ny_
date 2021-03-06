package test.me.libme.fn.netty.server;

import me.libme.fn.netty.server.ServerConfig;
import me.libme.fn.netty.server.SimpleHttpNioChannelServer;
import me.libme.kernel._c.json.JJSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by J on 2018/1/27.
 */
public class TestServer {


    private static final Logger LOGGER= LoggerFactory.getLogger(TestServer.class);

    public static void main(String[] args) {

        ServerConfig serverConfig=new ServerConfig();
        serverConfig.setHost("127.0.0.1");
        serverConfig.setPort(10089);
        serverConfig.setLoopThread(1);
        serverConfig.setWorkerThread(Runtime.getRuntime().availableProcessors());

        // START SERVER
        SimpleHttpNioChannelServer channelServer =
                new SimpleHttpNioChannelServer(serverConfig,request->{
                    if(request.getPath().equals("/_test4netty_/name")){
                        return "receive!";
                    }else{
                        return "aha....";
                    }

                });
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
