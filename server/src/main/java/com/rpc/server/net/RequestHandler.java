package com.rpc.server.net;

import com.common.baseinfo.Request;
import com.common.baseinfo.Response;
import com.common.baseinfo.ServiceObject;
import com.common.protocol.MessageProtocol;
import com.common.zk.ZkClientFactory;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * @author liaoyubo
 * @version 1.0
 * @date 2019/7/3
 * @description 请求处理类
 */
@Data
@ChannelHandler.Sharable
public class RequestHandler extends ChannelInboundHandlerAdapter {

    private MessageProtocol messageProtocol;

    private ZkClientFactory zkClientFactory;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("服务端开启");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("服务端开始读取数据");
        ByteBuf byteBuf = (ByteBuf) msg;
        byte[] requestByte = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(requestByte);
        byte[] res = handlerRequest(requestByte);
        ByteBuf resBuf = Unpooled.buffer(res.length);
        resBuf.writeBytes(res);
        byteBuf.release();
        ctx.write(resBuf);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("读取请求数据错误"+ cause);
        ctx.close();
    }

    private byte[] handlerRequest(byte[] requestByte) throws Exception {
        // 解码请求数据
        Request request = messageProtocol.unmarshallingRequest(requestByte);
        System.out.println("读取到的数据:"+request);
        // 获取服务对象
        ServiceObject serviceObject = zkClientFactory.getServiceObject(request.getServiceName());

        Response response = new Response();

        if (serviceObject == null) {
            response.setStatus(404);
        } else {
            // 反射调用方法
            Method method = serviceObject
                    .getInterfaceClass()
                    .getMethod(request.getMethodName(), request.getParameterTypes());
            Object returnValue = method.invoke(serviceObject.getClassObject(), request.getParameters());
            response.setStatus(500);
            response.setReturnValue(returnValue);
        }
        // 编组响应消息
        return messageProtocol.marshallingResponse(response);
    }
}
