package com.common.util;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Properties;

/**
 * @author liaoyubo
 * @version 1.0
 * @date 2019/7/1
 * @description
 */
@Slf4j
public class PropertiesUtils {

    private static PropertiesUtils instance = new PropertiesUtils();

    private Properties properties;

    private PropertiesUtils(){
        properties = new Properties();
        try {
            properties.load(PropertiesUtils.class.getResourceAsStream("/application.properties"));
        } catch (IOException e) {
            log.error("获取配置文件出错{}",e);
        }
    }

    public static String getProperties(String key){
        return instance.properties.getProperty(key);
    }

}
