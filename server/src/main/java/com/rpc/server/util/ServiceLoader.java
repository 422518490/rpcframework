package com.rpc.server.util;

import com.rpc.server.anntation.Service;
import io.netty.util.internal.StringUtil;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author liaoyubo
 * @version 1.0
 * @date 2019/7/4
 * @description
 */

public class ServiceLoader {

    public Map<String,Object> loadServiceClass(String packName) throws Exception {

        if (StringUtil.isNullOrEmpty(packName)){
            return null;
        }

        Map<String,Object> classMap = new HashMap<>();

        String[] classPath = packName.split(",");

        List<Class<?>> classList = new ArrayList<>();

        for (String path : classPath){
            List<Class<?>> classes = getClasses(path);
            if (classes != null && classes.size() > 0){
                classList.addAll(classes);
            }
        }

        // 循环实例化
        for(Class<?> cla : classList) {
            Object obj = cla.newInstance();
            // 获取实现注解的接口
            classMap.put(cla.getAnnotation(Service.class).value().getName(), obj);
        }

        return classMap;
    }

    public List<Class<?>> getClasses(String classPath) throws Exception {
        List<Class<?>> classList = new ArrayList<>();

        // 获取当前类加载器
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null){
            throw new Exception("无法获取当前类加载器");
        }

        String formatPath = classPath.replaceAll("\\.","/");

        URL resource = classLoader.getResource(formatPath);
        if (resource == null){
            throw new Exception("没有指定的文件路径{}"+formatPath);
        }

        File file = new File(resource.getFile());

        if (file.exists()){
            // 如果是文件夹
            if (file.isDirectory()){
                // 获取文件夹下面的所有文件
                File[] files = file.listFiles();
                for(int i = 0;i < files.length;i++){
                    if (files[i].isDirectory()){
                        List<Class<?>> classes = getClasses(files[i].getPath());
                        if (classes != null && classes.size() > 0){
                            classList.addAll(classes);
                        }
                    }else {
                        Class<?> clazz = seviceClass(files[i],classPath);
                        if (clazz != null){
                            classList.add(clazz);
                        }
                    }
                }
            }else if (file.isFile()){
                Class<?> clazz = seviceClass(file,classPath);
                if (clazz != null){
                    classList.add(clazz);
                }
            }
        }else {
            throw new Exception("文件包路径不存在");
        }
        return classList;
    }

    private Class<?> seviceClass(File file,String classPath) throws ClassNotFoundException {
        if (file.getName().endsWith(".class")){
            String[] fileName = file.getName().split(".class");
            Class<?> clazz = Class.forName(classPath + '.' + fileName[0]);
            if (clazz.getAnnotation(Service.class) != null){
                return clazz;
            }
        }
        return null;
    }
}
