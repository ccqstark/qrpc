package com.ccqstark.qrpc.core.loadbalance;

import com.ccqstark.qrpc.common.extension.SPI;
import com.ccqstark.qrpc.core.remoting.dto.RpcRequest;

import java.util.List;

/**
 * @author ccqstark
 * @description 负载均衡接口
 * @date 2022/6/29 22:53
 */
@SPI
public interface LoadBalance {
    /**
     * 从已存在的服务地址列表中选取一个
     *
     * @param serviceUrlList 服务地址列表（属于一个服务）
     * @param rpcRequest     rpc请求体
     * @return 目标地址
     */
    String selectServiceAddress(List<String> serviceUrlList, RpcRequest rpcRequest);
}
