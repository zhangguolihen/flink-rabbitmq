package com.larenzhang.connectors.http.source;

import com.larenzhang.connectors.rabbitmq.utils.GsonUtil;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.functions.source.ParallelSourceFunction;
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
 * 自定义实现一个支持并行度的source
 *
 */
public class MyHttpSource implements ParallelSourceFunction<String> {

    private long count = 1L;

    private boolean isRunning = true;
    private static  int HTTP_PORT = 80;
    private static String PATTERN="*";
    private static HttpServer httpServer;
    private static CollectingHttpRequestHandler<String> requestHandler =
            new  CollectingHttpRequestHandler<>(new SimpleStringSchema());


    public MyHttpSource(int listenPort,String pattern){
        HTTP_PORT=listenPort;
        PATTERN=pattern;
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
        if(null==httpServer) httpServer = createHttpServer(HTTP_PORT, requestHandler);
        while(isRunning){
            String rs=requestHandler.getCollectedData().take();
            ctx.collect(rs);
            System.out.println("source"+GsonUtil.toJson(rs));
            count++;

        }
    }
    public static HttpServer createHttpServer(int listenPort, HttpRequestHandler requestHandler)
            throws IOException {

        SocketConfig socketConfig = SocketConfig.custom()
                .setSoTimeout(15)
                .setTcpNoDelay(true)
                .setSoReuseAddress(true)
                .build();

        final HttpServer server = ServerBootstrap.bootstrap()
                .setListenerPort(listenPort)
                .setLocalAddress(InetAddress.getLoopbackAddress())
                //.setHttpProcessor(httpProcessor)
                .setSocketConfig(socketConfig)
                .registerHandler(PATTERN, requestHandler)
                //.setResponseFactory(new DefaultHttpResponseFactory())
                .create();

        server.start();

        return server;
    }
    /**
     * 取消一个cancel的时候会调用的方法
     *
     */
    @Override
    public void cancel() {
        isRunning = false;
        httpServer.shutdown(10, TimeUnit.MILLISECONDS);
    }


    private static class CollectingHttpRequestHandler<T> implements HttpRequestHandler {
        private static final Logger LOG = LoggerFactory.getLogger(CollectingHttpRequestHandler.class);
        private BlockingQueue<T> queue=new LinkedBlockingQueue<>(1);
        private DeserializationSchema<T> deserializationSchema;

        public CollectingHttpRequestHandler(DeserializationSchema<T> deserializationSchema) {
            this.deserializationSchema = deserializationSchema;
        }

        @Override
        public void handle(HttpRequest request, HttpResponse response, HttpContext context)
                throws HttpException, IOException {

            String method = request.getRequestLine().getMethod().toUpperCase(Locale.ROOT);
            if (!method.equals("POST")) {
                throw new MethodNotSupportedException(method + " method not supported");
            }

            if (request instanceof HttpEntityEnclosingRequest) {
                HttpEntity entity = ((HttpEntityEnclosingRequest) request).getEntity();
                byte[] entityContent = EntityUtils.toByteArray(entity);

                T data = deserializationSchema.deserialize(entityContent);

                LOG.debug("Incoming entity content (bytes): " + entityContent.length);
                LOG.debug(data.toString());
                try {
                    queue.put(data);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                response.setStatusCode(200);
                byte[] serializedValue = new SimpleStringSchema().serialize("{ok}");
                ByteArrayEntity httpEntity = new ByteArrayEntity(serializedValue);
                response.setEntity(httpEntity);
                response.setHeader("Content-Type","application/json");

            }
        }

        public BlockingQueue<T> getCollectedData() {
            return queue;
        }
    }
}
