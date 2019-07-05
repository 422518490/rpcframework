package com.common.baseinfo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liaoyubo
 * @version 1.0
 * @date 2019/7/1
 * @description 远程服务信息
 */
@Data
public class ServiceInfo {
    /**
     * service名称
     */
    private String serviceName;

    /**
     *服务提供的协议
     */
    private String protocol;

    /**
     * 服务的调用地址
     */
    private List<String> address;

    public void addAddress(String address){
        if(this.address == null) {
            this.address = new ArrayList();
        }
        this.address.add(address);
    }
}
