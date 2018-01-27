package me.libme.fn.netty.server;

/**
 * Created by J on 2017/9/7.
 */
public interface RequestMappingHandler {

    Object handle(HttpRequest httpRequest) throws Exception;

}
