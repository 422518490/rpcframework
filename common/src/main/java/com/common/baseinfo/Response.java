package com.common.baseinfo;

import lombok.Data;

/**
 * @author liaoyubo
 * @version 1.0
 * @date 2019/7/2
 * @description
 */
@Data
public class Response {

    /**
     * 返回状态值
     */
    private Integer status;

    /**
     * 返回值
     */
    private Object returnValue;

    /**
     * 异常信息
     */
    private Exception exception;
}
