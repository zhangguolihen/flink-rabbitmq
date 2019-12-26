package com.larenzhang.connectors.http.source;

import com.larenzhang.connectors.http.server.HttpNtServer;
import com.larenzhang.connectors.rabbitmq.utils.GsonUtil;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.functions.source.ParallelSourceFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.http.*;
import org.apache.http.config.SocketConfig;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.bootstrap.HttpServer;
import org.apache.http.impl.bootstrap.ServerBootstrap;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Locale;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 自定义httpsource
 *
 */
public class NtHttpSource implements SourceFunction<String> {

    private long count = 0L;

    private boolean isRunning = true;
    private static  int HTTP_PORT = 8068;
    private static  String PATTERN="*";
    private  transient HttpNtServer httpNtServer=null;
    public static BlockingQueue<String> queue=new LinkedBlockingQueue<>(1000);


    public NtHttpSource(int listenPort, String pattern){
        HTTP_PORT=listenPort;
        this.PATTERN=pattern;
    }
    /**
     * 主要的方法
     * 启动一个source
     * 大部分情况下，都需要在这个run方法中实现一个循环，这样就可以循环产生数据了
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void run(SourceContext<String> ctx) throws Exception {
         httpNtServer= new HttpNtServer();
         httpNtServer.createHttpServer(HTTP_PORT);
        while(isRunning){
            String rs=queue.take();
            ctx.collect(queue.take());
            count++;
            System.out.println(count+":source"+GsonUtil.toJson(rs));

        }
    }

    /**
     * 取消一个cancel的时候会调用的方法
     *
     */
    @Override
    public void cancel() {
        isRunning = false;
        httpNtServer.closeHttpServer();
    }

}
