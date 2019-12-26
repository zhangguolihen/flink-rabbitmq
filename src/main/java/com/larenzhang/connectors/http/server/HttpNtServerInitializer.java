package com.larenzhang.connectors.http.server;

import org.apache.flink.shaded.akka.org.jboss.netty.handler.ssl.SslContext;
import org.apache.flink.shaded.netty4.io.netty.channel.ChannelHandler;
import org.apache.flink.shaded.netty4.io.netty.channel.ChannelInitializer;
import org.apache.flink.shaded.netty4.io.netty.channel.ChannelPipeline;
import org.apache.flink.shaded.netty4.io.netty.channel.socket.SocketChannel;
import org.apache.flink.shaded.netty4.io.netty.handler.codec.http.*;

/**
 * @Description
 * @Author zhangguoli
 * @Date 16:38 2019/9/28
 **/
public class HttpNtServerInitializer extends ChannelInitializer<SocketChannel> {

    private  SslContext sslCtx=null;
    private   String  HOST;
    private    int PORT ;

    public HttpNtServerInitializer(SslContext sslCtx,String host,int port) {
        this.sslCtx = sslCtx;
        this.HOST=host;
        this.PORT=port;
    }

    public HttpNtServerInitializer() {

    }
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
      ChannelPipeline cp=ch.pipeline();
       if(sslCtx!=null){
           cp.addLast((ChannelHandler) sslCtx.newHandler(HOST,PORT));
       }

       cp.addLast("encode",new HttpResponseEncoder());
       cp.addLast("decode",new HttpRequestDecoder());
       cp.addLast("aggre",new HttpObjectAggregator(10*1024*1024));
       cp.addLast("compressor",new HttpContentCompressor());
       cp.addLast("serverHandler",new HttpNtServerHandler());
    }
}
    