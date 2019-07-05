package com.common.protocol;

import com.common.baseinfo.Request;
import com.common.baseinfo.Response;

/**
 * @author liaoyubo
 * @version 1.0
 * @date 2019/7/2
 * @description
 */
public interface MessageProtocol {

    /**
     * 编组请求消息
     * @param req
     * @return
     */
    byte[] marshallingRequest(Request req);

    /**
     * 解编组请求消息
     * @param data
     * @return
     */
    Request unmarshallingRequest(byte[] data);

    /**
     * 编组响应消息
     * @param rsp
     * @return
     */
    byte[] marshallingResponse(Response rsp);

    /**
     * 解编组响应消息
     * @param data
     * @return
     */
    Response unmarshallingResponse(byte[] data);
}
