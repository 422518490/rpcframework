package com.common.baseinfo;

import lombok.Data;

/**
 * @author liaoyubo
 * @version 1.0
 * @date 2019/7/2
 * @description
 */
@Data
public class Request {

    /**
     * 接口类
     */
    private String serviceName;

    /**
     * 接口方法
     */
    private String methodName;

    /**
     * 参数类型
     */
    private Class<?>[] parameterTypes;

    /**
     * 参数值
     */
    private Object[] parameters;

}
