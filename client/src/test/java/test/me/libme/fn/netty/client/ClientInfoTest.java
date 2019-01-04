package test.me.libme.fn.netty.client;

import me.libme.fn.netty.client.SimpleChannelExecutor;
import me.libme.fn.netty.client.SimpleClientFactory;
import me.libme.fn.netty.msg.BodyDecoder;
import me.libme.kernel._c.json.JJSON;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by J on 2017/9/7.
 */

public class ClientInfoTest {

    private static final Logger LOGGER= LoggerFactory.getLogger(ClientInfoTest.class);

    private SimpleChannelExecutor simpleChannelExecutor;

    private final BodyDecoder.SimpleJSONDecoder<String> DECODER=new BodyDecoder.SimpleJSONDecoder(String.class);


    private ExecutorService executorService= Executors.newFixedThreadPool(20);

    @Before
    public void prepare(){
        simpleChannelExecutor= new SimpleChannelExecutor("127.0.0.1",10089);
    }

    @Test
    public void call(){

        Info info= SimpleClientFactory.factory(Info.class,"/demo/info",simpleChannelExecutor);
        MapInfo mapInfo= SimpleClientFactory.factory(MapInfo.class,"/demo/map",simpleChannelExecutor);


        for(int i=0;i<1000;i++){
            LOGGER.debug("AHA : "+i);
            executorService.execute(new Runnable() {
                @Override
                public void run() {

                    final String name = "J-" + new Random().nextInt(9999);
                    int age=20;

                    String value=info.info(name,age,null);

                    LOGGER.info("response : " + value + "; request : " + name);

                    Map<String,Object> map= mapInfo.info(name,age,null);
                    LOGGER.info("object : "+ JJSON.get().format(map));

                }
            });
        }



        LOGGER.info("end-");
        try {
            Thread.sleep(1000*60*60);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }



}
