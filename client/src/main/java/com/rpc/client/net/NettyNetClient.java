package com.rpc.client.net;

import com.common.baseinfo.ServiceInfo;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author liaoyubo
 * @version 1.0
 * @date 2019/7/3
 * @description netty方式实现客户端连接
 */
public class NettyNetClient implements NetClient {

    Lock lock = new ReentrantLock();

    @Override
    public byte[] sendRequest(byte[] data, ServiceInfo serviceInfo) {
        List<String> addresses = serviceInfo.getAddress();
        Random random = new Random();
        String address = addresses.get(random.nextInt(addresses.size()));
        String[] ipPort = address.split(":");

        lock.lock();
        ConnectHandler connectHandler = new ConnectHandler(data);
        try {
            new Thread(() -> {
                System.out.println("启动netty");
                new NettyConnecter().nettyConnect(ipPort[0],Integer.parseInt(ipPort[1]),connectHandler);
            }).start();
            byte[] respData = connectHandler.getRespMsg();
            // 关闭本次传输
            connectHandler.close();
            return respData;
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
        return null;
    }

    class NettyConnecter{

        public void nettyConnect(String ip,int port,ConnectHandler connectHandler){
            EventLoopGroup clientGroup = new NioEventLoopGroup();
            Bootstrap bootstrap = new Bootstrap();

            try {
                bootstrap.group(clientGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY,true)
                    .handler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline channelPipeline = socketChannel.pipeline();
                            channelPipeline.addLast(connectHandler);
                        }
                    });

                // 启动连接
                ChannelFuture channelFuture = bootstrap.connect(ip,port);
                // 等待客户端连接关闭
                channelFuture.channel().closeFuture().sync();
                System.out.println(Thread.currentThread().getName()+" netty 通道即将关闭");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                System.out.println(Thread.currentThread().getName()+" netty释放资源");
                clientGroup.shutdownGracefully();
            }
        }
    }
}
