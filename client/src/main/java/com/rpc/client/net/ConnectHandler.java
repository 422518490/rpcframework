package com.rpc.client.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;

/**
 * @author liaoyubo
 * @version 1.0
 * @date 2019/7/3
 * @description
 */
@Data
public class ConnectHandler extends ChannelInboundHandlerAdapter {

    private CountDownLatch countDownLatch = null;

    private ChannelHandlerContext ctx;

    private byte[] data;

    private byte[] respMsg;

    public ConnectHandler(byte[] data){
        this.data = data;
        countDownLatch = new CountDownLatch(1);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("连接服务端成功"+ ctx);
        this.ctx = ctx;
        ByteBuf byteBuf = Unpooled.buffer(data.length);
        byteBuf.writeBytes(data);
        System.out.println("客户端发送消息："+ byteBuf);
        ctx.writeAndFlush(byteBuf);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("客户端获取到的数据"+msg);
        ByteBuf byteBuf = (ByteBuf)msg;
        byte[] respByte = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(respByte);
        respMsg = respByte;
        byteBuf.release();
        countDownLatch.countDown();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("netty异常:"+ cause);
        ctx.close();
    }

    public byte[] getRespMsg(){
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return respMsg;
    }

    public void close(){
        ctx.close();
    }
}
