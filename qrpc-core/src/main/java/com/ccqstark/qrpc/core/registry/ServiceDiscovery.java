package com.ccqstark.qrpc.core.registry;

import com.ccqstark.qrpc.common.extension.SPI;
import com.ccqstark.qrpc.core.remoting.dto.RpcRequest;

import java.net.InetSocketAddress;

/**
 * @author ccqstark
 * @description 服务发现
 * @date 2022/6/19 16:00
 */
@SPI
public interface ServiceDiscovery {

    /**
     * 根据服务名称查找服务
     *
     * @param rpcRequest rpc请求对象
     * @return 服务地址
     */
    InetSocketAddress lookupService(RpcRequest rpcRequest);

}
