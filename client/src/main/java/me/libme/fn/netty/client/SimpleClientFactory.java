package me.libme.fn.netty.client;

import java.lang.reflect.Proxy;

/**
 * Created by J on 2018/1/29.
 */
public class SimpleClientFactory {


    public static  <T> T factory(Class<T> intarface,String path,ClientChannelExecutor<NioChannelRunnable> channelExecutor){

        SimpleClient simpleClient=new SimpleClient(channelExecutor,path);
        return (T)Proxy.newProxyInstance(intarface.getClassLoader(),new Class[]{intarface},simpleClient);

    }






}
