package com.rpc.server.anntation;

import java.lang.annotation.*;

/**
 * @author liaoyubo
 * @version 1.0
 * @date 2019/7/3
 * @description
 */
@Target(ElementType.TYPE)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Service {
    /**
     * 注解所属接口类型
     * @return
     */
    Class<?> value();
}
