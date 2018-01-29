package test.me.libme.fn.netty.client;

import java.util.Map;

/**
 * Created by J on 2018/1/29.
 */
public interface MapInfo {

    Map<String,Object> info(String name, int age, Object httpRequest);

}
