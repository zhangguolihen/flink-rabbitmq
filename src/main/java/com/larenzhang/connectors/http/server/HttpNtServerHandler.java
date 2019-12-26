package com.larenzhang.connectors.http.server;


import com.larenzhang.connectors.http.source.NtHttpSource;
import org.apache.flink.shaded.netty4.io.netty.buffer.Unpooled;
import org.apache.flink.shaded.netty4.io.netty.channel.ChannelFutureListener;
import org.apache.flink.shaded.netty4.io.netty.channel.ChannelHandlerContext;
import org.apache.flink.shaded.netty4.io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.flink.shaded.netty4.io.netty.handler.codec.http.*;
import org.apache.flink.shaded.netty4.io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description
 * @Author zhangguoli
 * @Date 17:09 2019/9/28
 **/
public class HttpNtServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(HttpNtServerHandler.class);
    @Override
    public void channelRead(ChannelHandlerContext ct, Object msg) throws Exception {

        String result="OK";
        FullHttpRequest httpRequest= (FullHttpRequest) msg;
        String path=httpRequest.uri();
        String body=httpRequest.content().toString(CharsetUtil.UTF_8);
        try {
            HttpMethod method = httpRequest.method();

            if(!path.startsWith("/u/p")){
                result="请求地址不正确";
                sendPlain(result, ct, HttpResponseStatus.BAD_REQUEST);
            }
            if (HttpMethod.GET.equals(method)) {
                result = "<table><tr><td>当前处理队列数据量：</td><td>"+ NtHttpSource.queue.size()+"</td></tr></table>";
                sendPlain(result, ct, HttpResponseStatus.OK);
            }

            if (HttpMethod.POST.equals(method)) {
                NtHttpSource.queue.put(body);
                send(result, ct, HttpResponseStatus.OK);
            }


        }catch (Exception e){
            e.printStackTrace();
        }finally {
            httpRequest.release();
        }

    }

    /**
     * 建立连接返回消息
     * @param ct
     */
    @Override
    public  void channelActive(ChannelHandlerContext ct ){

        LOG.info("客户端地址"+ct.channel().remoteAddress());
    }

    /**
     * 异常处理
     * @param ct
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ct,Throwable cause) throws  Exception{
        cause.printStackTrace();
        ct.close();
    }


    private void send(String content, ChannelHandlerContext ct, HttpResponseStatus status){
        FullHttpResponse response=new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,status, Unpooled.copiedBuffer(content, CharsetUtil.UTF_8));

        response.headers().set(HttpHeaderNames.CONTENT_TYPE,"application/json;chartset=UTF-8");
        ct.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);

    }
    private void sendPlain(String content, ChannelHandlerContext ctx,
                      HttpResponseStatus status){
        FullHttpResponse response =
                new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,status,
                        Unpooled.copiedBuffer(content,CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE,
                " text/html;charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);

    }

}
    