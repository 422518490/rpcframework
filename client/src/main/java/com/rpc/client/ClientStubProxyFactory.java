package com.rpc.client;

import com.common.baseinfo.Request;
import com.common.baseinfo.Response;
import com.common.baseinfo.ServiceInfo;
import com.common.protocol.MessageProtocol;
import com.common.zk.ZkClientFactory;
import com.rpc.client.net.NetClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author liaoyubo
 * @version 1.0
 * @date 2019/7/2
 * @description
 */
@Data
public class ClientStubProxyFactory {

    private ZkClientFactory zkClientFactory;

    private MessageProtocol messageProtocol;

    private NetClient netClient;

    private Map<Class<?>, Object> objectCache = new HashMap();



    /**
     * 获取代理对象
     * @param interfaceClass
     * @param <T>
     * @return
     */
    public <T> T getProxy(Class<T> interfaceClass){
        T obj = (T) objectCache.get(interfaceClass);
        if (obj == null){
            obj  = (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(),
                    new Class[]{interfaceClass},
                    new ClientStubInvocationHandler(interfaceClass));
            this.objectCache.put(interfaceClass,obj);
        }
        return obj;
    }

    class ClientStubInvocationHandler implements InvocationHandler{

        private Class<?> interfaceClass;

        public ClientStubInvocationHandler(Class<?> interfaceClass){
            super();
            this.interfaceClass = interfaceClass;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // 获取zookeeper中的信息
            String interfaceName = interfaceClass.getName();
            ServiceInfo serviceInfo = zkClientFactory.getServiceInfo(interfaceName);

            if (!Optional.ofNullable(serviceInfo).isPresent()){
                throw new Exception("远程服务操作对象不存在");
            }

            // 组装请求对象
            Request request = new Request();
            request.setServiceName(serviceInfo.getServiceName());
            request.setMethodName(method.getName());
            request.setParameterTypes(method.getParameterTypes());
            request.setParameters(args);

            // 编码请求消息
            byte[] requestBytes = messageProtocol.marshallingRequest(request);

            // 发送请求消息
            byte[] respBytes = netClient.sendRequest(requestBytes,serviceInfo);

            // 解码返回消息
            Response response = messageProtocol.unmarshallingResponse(respBytes);

            return response.getReturnValue();
        }
    }

}
