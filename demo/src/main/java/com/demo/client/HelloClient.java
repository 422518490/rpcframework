package com.demo.client;

import com.common.protocol.JsonMessageProtocol;
import com.common.zk.ZkClientFactory;
import com.demo.HelloService;
import com.rpc.client.ClientStubProxyFactory;
import com.rpc.client.net.NettyNetClient;
import lombok.extern.slf4j.Slf4j;

/**
 * @author liaoyubo
 * @version 1.0
 * @date 2019/7/4
 * @description
 */
public class HelloClient{

    public static void main(String [] args){
        ClientStubProxyFactory client = new ClientStubProxyFactory();
        client.setMessageProtocol(new JsonMessageProtocol());
        client.setNetClient(new NettyNetClient());
        client.setZkClientFactory(new ZkClientFactory());

        HelloService helloService = client.getProxy(HelloService.class);

        String world = helloService.sayHello("world");
        System.out.println(world);

        String kitty = helloService.sayHello("kitty");
        System.out.println(kitty);
    }

}
