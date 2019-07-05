package com.rpc.client.net;

import com.common.baseinfo.ServiceInfo;

/**
 * @author liaoyubo
 * @version 1.0
 * @date 2019/7/3
 * @description
 */
public interface NetClient {

    /**
     * 发送请求消息
     *
     * @param data
     * @param serviceInfo
     * @return
     */
    byte[] sendRequest(byte[] data, ServiceInfo serviceInfo);
}
