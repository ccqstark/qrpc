package com.ccqstark.qrpc.core.provider;

import com.ccqstark.qrpc.core.annotation.RpcService;
import com.ccqstark.qrpc.core.config.RpcServiceConfig;

/**
 * @author ccqstark
 * @description 存储与服务提供接口
 * @date 2022/7/6 23:20 
 */
public interface ServiceProvider {

    /**
     * @param rpcServiceConfig rpc服务相关属性
     */
    void addService(RpcServiceConfig rpcServiceConfig);

    /**
     * @param rpcServiceName rpc服务名称
     * @return 服务对象
     */
    Object getService(String rpcServiceName);

    /**
     * @param rpcServiceConfig rpc服务相关属性
     */
    void publishService(RpcServiceConfig rpcServiceConfig);

}
