package com.ccqstark.qrpc.core.registry;

import com.ccqstark.qrpc.common.extension.SPI;

import java.net.InetSocketAddress;

/**
 * @author ccqstark
 * @description 服务注册
 * @date 2022/6/19 15:57
 */
@SPI
public interface ServiceRegistry {

    /**
     * 注册服务
     *
     * @param rpcServiceName    rpc服务名称
     * @param inetSocketAddress 服务地址
     */
    void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress);

}
