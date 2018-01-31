package me.libme.fn.netty.server;

import me.libme.xstream.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by J on 2018/1/20.
 */
public class RequestProcessor {


    private RequestProcessorBuilder recProcessorBuilder;

    private WindowTopology windowTopology;

    private RequestProcessor(){}

    public static RequestProcessorBuilder builder(){
        return new RequestProcessorBuilder();
    }


    public void shutdown(){
        windowTopology.shutdown();
    }


    public void start(){

        QueueWindowSourcer queueWindowSourcer=new QueueWindowSourcer(recProcessorBuilder.queueHolder.queue());

        WindowTopology.WindowBuilder windowBuilder=WindowTopology.builder().setName("Request Handler Topology")
                .setCount(recProcessorBuilder.count)
                .setTime(recProcessorBuilder.time)
                .windowExecutor(recProcessorBuilder.windowExecutor)
                .executor(recProcessorBuilder.executor)
                .setSourcer(queueWindowSourcer);


        for(OperationProvider operationProvider:recProcessorBuilder.operationProviders){
            windowBuilder.addOperation(operationProvider.provide());
        }

        windowTopology=windowBuilder.build();
        windowTopology.start();

    }


    public static class RequestProcessorBuilder{

        private QueueHolder queueHolder;

        private List<OperationProvider> operationProviders=new ArrayList<>();

        private String[] args=new String[]{};

        private int count=1000 ;

        private int time=1*1000; //millisecond

        private ScheduledExecutorService windowExecutor;

        private ExecutorService executor;

        /**
         * !important / micro-batch
         * @param windowExecutor
         * @return
         */
        public RequestProcessorBuilder windowExecutor(ScheduledExecutorService windowExecutor) {
            this.windowExecutor = windowExecutor;
            return this;
        }

        /**
         * !important / executing thread pool
         * @param executor
         * @return
         */
        public RequestProcessorBuilder executor(ExecutorService executor) {
            this.executor = executor;
            return this;
        }

        public RequestProcessorBuilder setQueueHolder(QueueHolder queueHolder) {
            this.queueHolder = queueHolder;
            return this;
        }

        public RequestProcessorBuilder setCount(int count) {
            this.count = count;
            return this;
        }

        public RequestProcessorBuilder setTime(int time) {
            this.time = time;
            return this;
        }

        public RequestProcessorBuilder setArgs(String[] args) {
            this.args = args;
            return this;
        }

        public RequestProcessorBuilder addConsumerProider(ConsumerProider consumerProider){
            operationProviders.add(consumerProider);
            return this;
        }

        public RequestProcessorBuilder addFilterProider(FilterProider filterProider){
            operationProviders.add(filterProider);
            return this;
        }

        public RequestProcessor build(){
            RequestProcessor recProcessor=new RequestProcessor();
            recProcessor.recProcessorBuilder=this;
            return recProcessor;
        }


    }

    public interface OperationProvider<T extends Operation>{

        T provide();

    }

    public interface ConsumerProider extends OperationProvider<Consumer>{

    }

    public interface FilterProider extends OperationProvider<Filter>{

    }





}
