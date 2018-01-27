package me.libme.fn.netty.server;

import java.util.Collection;

/**
 * Created by J on 2017/9/7.
 */
public interface HttpRequest {

    public String getHeader(String name);

    public String getParam(String name);

    public String[] getParams(String name);

    boolean containsParam(String name);

    Collection<String> paramNames();

    public String getPath();

    public String getUrl();

    HttpResponse getHttpResponse();


}
