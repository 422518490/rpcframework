package com.demo.server;

import com.rpc.server.RpcStrap;

/**
 * @author liaoyubo
 * @version 1.0
 * @date 2019/7/4
 * @description
 */
public class HelloServer {

    public static void main(String[] args){
        RpcStrap rpcStrap = new RpcStrap();
        try {
            rpcStrap.start("com.demo.server");
            System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
