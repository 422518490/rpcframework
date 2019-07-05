package com.common.baseinfo;

import lombok.Data;

/**
 * @author liaoyubo
 * @version 1.0
 * @date 2019/7/1
 * @description 服务端实例对象
 */
@Data
public class ServiceObject {

    /**
     * 服务名称
     */
    private String className;

    /**
     * 接口
     */
    private Class<?> interfaceClass;

    /**
     * 对象实例
     */
    private Object classObject;

}
