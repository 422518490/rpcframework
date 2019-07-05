package com.common.protocol;

import com.alibaba.fastjson.JSON;
import com.common.baseinfo.Request;
import com.common.baseinfo.Response;

/**
 * @author liaoyubo
 * @version 1.0
 * @date 2019/7/2
 * @description
 */
public class JsonMessageProtocol implements MessageProtocol {

    @Override
    public byte[] marshallingRequest(Request req) {
        if (req.getParameters() != null){
            Object[] parameters = req.getParameters();
            for (int i = 0;i < parameters.length;i++){
                parameters[i] = JSON.toJSONString(parameters[i]);
            }
        }

        return JSON.toJSONBytes(req);
    }

    @Override
    public Request unmarshallingRequest(byte[] data) {
        Request request = JSON.parseObject(data,Request.class);
        if (request.getParameters() != null){
            Object[] parameters = request.getParameters();
            for (int i = 0;i < parameters.length;i++){
                parameters[i] = JSON.parseObject(parameters[i] + "",Object.class);
            }
            request.setParameters(parameters);
        }
        return request;
    }

    @Override
    public byte[] marshallingResponse(Response rsp) {
        return JSON.toJSONBytes(rsp);
    }

    @Override
    public Response unmarshallingResponse(byte[] data) {
        return JSON.parseObject(data,Response.class);
    }
}
