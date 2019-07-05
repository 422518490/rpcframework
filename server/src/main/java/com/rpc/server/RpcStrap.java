package com.rpc.server;

import com.common.baseinfo.ServiceObject;
import com.common.protocol.JsonMessageProtocol;
import com.common.zk.ZkClientFactory;
import com.rpc.server.net.RequestHandler;
import com.rpc.server.net.RpcServer;
import com.rpc.server.util.ServiceLoader;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author liaoyubo
 * @version 1.0
 * @date 2019/7/3
 * @description
 */
@Data
public class RpcStrap {

    int port = 9070;

    public void start(String packName) throws Exception {
        // 获取所有的包路径下面含有@Service注解的类，并注册到zookeeper上
        ServiceLoader loader = new ServiceLoader();
        ZkClientFactory zkClientFactory = new ZkClientFactory();
        Map<String,Object> classMap = loader.loadServiceClass(packName);

        classMap.forEach((key,value) ->{
            Class<?> interf = null;
            Class<?>[] interfaces = value.getClass().getInterfaces();
            for(Class<?> inter : interfaces) {
                if(key.equals(inter.getName())) {
                    interf = inter;
                }
            }

            ServiceObject serviceObject = new ServiceObject();
            serviceObject.setClassName(key);
            serviceObject.setInterfaceClass(interf);
            serviceObject.setClassObject(value);
            try {
                zkClientFactory.register(serviceObject,port);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // 启动服务
        RpcServer rpcServer = new RpcServer(port);
        RequestHandler requestHandler = new RequestHandler();
        requestHandler.setMessageProtocol(new JsonMessageProtocol());
        requestHandler.setZkClientFactory(zkClientFactory);
        rpcServer.setRequestHandler(requestHandler);
        rpcServer.setPort(port);
        rpcServer.start();
    }

}
