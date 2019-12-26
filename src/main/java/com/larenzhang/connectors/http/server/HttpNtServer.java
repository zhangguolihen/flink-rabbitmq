package com.larenzhang.connectors.http.server;

import org.apache.flink.shaded.netty4.io.netty.bootstrap.ServerBootstrap;
import org.apache.flink.shaded.netty4.io.netty.channel.*;
import org.apache.flink.shaded.netty4.io.netty.channel.nio.NioEventLoopGroup;
import org.apache.flink.shaded.netty4.io.netty.channel.socket.SocketChannel;
import org.apache.flink.shaded.netty4.io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.flink.shaded.netty4.io.netty.handler.logging.LogLevel;
import org.apache.flink.shaded.netty4.io.netty.handler.logging.LoggingHandler;

/**
 * @Description
 * @Author zhangguoli
 * @Date 16:13 2019/9/28
 **/
public class HttpNtServer {
    private static ServerBootstrap bootstrap=new ServerBootstrap();
    private static EventLoopGroup group= new NioEventLoopGroup();

    public static void createHttpServer(int listenPort)
            throws  InterruptedException {
        createHttpServer(listenPort,new HttpNtServerInitializer());
    }

    public static void createHttpServer(int listenPort, ChannelInitializer<SocketChannel>  serverHandlerInit)
            throws  InterruptedException {

        bootstrap.group(group).channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(serverHandlerInit)
                .option(ChannelOption.SO_BACKLOG,1024)//设置tcp协议的请求等待队列
                .option(ChannelOption.TCP_NODELAY,true)
                .option(ChannelOption.SO_REUSEADDR,true)
                .childOption(ChannelOption.SO_KEEPALIVE,true);

        bootstrap.bind(listenPort).sync().channel();
        //  ch.closeFuture().sync();
    }
    public static void closeHttpServer(){
        group.shutdownGracefully();
    }


}
    