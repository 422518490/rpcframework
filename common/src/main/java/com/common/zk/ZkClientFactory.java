package com.common.zk;

import com.alibaba.fastjson.JSON;
import com.common.baseinfo.ServiceInfo;
import com.common.baseinfo.ServiceObject;
import com.common.serialize.MyZkSerializer;
import com.common.util.PropertiesUtils;
import org.I0Itec.zkclient.ZkClient;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * @author liaoyubo
 * @version 1.0
 * @date 2019/7/1
 * @description
 */
public class ZkClientFactory {

    private ZkClient zkClient;

    private final String rootPath = "/rpc";

    private HashMap<String, ServiceObject> serviceMap = new HashMap<>();

    private String protocol;

    public ZkClientFactory() {
        zkClient = new ZkClient(PropertiesUtils.getProperties("zk.address"));
        zkClient.setZkSerializer(new MyZkSerializer());
    }

    public void register(ServiceObject serviceObject, int port) throws Exception {
        serviceMap.put(serviceObject.getClassName(), serviceObject);

        ServiceInfo serviceInfo = new ServiceInfo();
        serviceInfo.setServiceName(serviceObject.getClassName());
        serviceInfo.setProtocol(protocol);
        String address = InetAddress.getLocalHost().getHostAddress();
        serviceInfo.addAddress(address + ":" + port);
        register(serviceInfo);
    }

    public void register(ServiceInfo serviceInfo) throws Exception {

        boolean rootExists = zkClient.exists(rootPath);
        if (!rootExists) {
            // 创建永久节点
            zkClient.createPersistent(rootPath);
        }

        String nodePath = rootPath + "/" + serviceInfo.getServiceName() + "/service";
        boolean nodePathExists = zkClient.exists(nodePath);
        if (!nodePathExists) {
            zkClient.createPersistent(nodePath,true);
        }
        String infoJson = JSON.toJSONString(serviceInfo);
        infoJson = URLEncoder.encode(infoJson, "utf-8");
        // 创建临时节点
        zkClient.createEphemeral(nodePath + "/" + infoJson);

    }

    /**
     * 获取指定节点下面的ServiceInfo集合信息
     *
     * @param serviceName
     * @return
     * @throws UnsupportedEncodingException
     */
    public List<ServiceInfo> findServiceInfoByServiceName(String serviceName) throws UnsupportedEncodingException {
        String nodePath = rootPath + "/" + serviceName + "/service";
        List<String> childern = zkClient.getChildren(nodePath);
        List<ServiceInfo> serviceInfoList = new ArrayList<ServiceInfo>();
        for (String child : childern) {
            child = URLDecoder.decode(child, "utf-8");
            ServiceInfo serviceInfo = JSON.parseObject(child, ServiceInfo.class);
            serviceInfoList.add(serviceInfo);
        }
        return serviceInfoList;
    }

    /**
     * 获取远程接口对象信息
     *
     * @param serviceName
     * @return
     * @throws UnsupportedEncodingException
     */
    public ServiceInfo getServiceInfo(String serviceName) throws UnsupportedEncodingException {
        List<ServiceInfo> serviceInfoList = findServiceInfoByServiceName(serviceName);
        if (!Optional.ofNullable(serviceInfoList).isPresent()
                || serviceInfoList.size() == 0) {
            return null;
        }
        ServiceInfo serviceInfo = serviceInfoList.get(0);
        int size = serviceInfoList.size();
        if (size > 1) {
            for (int i = 1; i < size; i++) {
                String address = serviceInfoList.get(i).getAddress().get(0);
                serviceInfo.addAddress(address);
            }
        }

        return serviceInfo;
    }

    public ServiceObject getServiceObject(String className) {
        return serviceMap.get(className);
    }

}
