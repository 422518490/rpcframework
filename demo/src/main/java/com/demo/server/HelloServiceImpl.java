package com.demo.server;

import com.demo.HelloService;
import com.rpc.server.anntation.Service;

/**
 * @author liaoyubo
 * @version 1.0
 * @date 2019/7/4
 * @description
 */
@Service(value = HelloService.class)
public class HelloServiceImpl implements HelloService {

    @Override
    public String sayHello(String word) {
        return "hello " + word;
    }
}
