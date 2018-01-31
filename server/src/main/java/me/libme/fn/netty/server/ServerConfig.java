package me.libme.fn.netty.server;


import me.libme.kernel._c._m.JModel;

/**
 * Created by J on 2017/9/7.
 * @see RequestProcessor
 */
public class ServerConfig implements JModel {

    private String host;

    private int port;

    private int loopThread;

    private int workerThread;

    /*
     window batch begin config  see RequestProcessor
    */

    private int windowCount=10 ;

    private int windowTime=1*100; //millisecond

    /*
     window batch end config
    */

    public int getWindowCount() {
        return windowCount;
    }

    public void setWindowCount(int windowCount) {
        this.windowCount = windowCount;
    }

    public int getWindowTime() {
        return windowTime;
    }

    public void setWindowTime(int windowTime) {
        this.windowTime = windowTime;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getLoopThread() {
        return loopThread;
    }

    public void setLoopThread(int loopThread) {
        this.loopThread = loopThread;
    }

    public int getWorkerThread() {
        return workerThread;
    }

    public void setWorkerThread(int workerThread) {
        this.workerThread = workerThread;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }



}
