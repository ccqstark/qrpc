package com.ccqstark.qrpc.core.registry.zk;

import com.ccqstark.qrpc.common.enums.RpcErrorMessageEnum;
import com.ccqstark.qrpc.common.exception.RpcException;
import com.ccqstark.qrpc.common.extension.ExtensionLoader;
import com.ccqstark.qrpc.common.utils.CollectUtil;
import com.ccqstark.qrpc.core.loadbalance.LoadBalance;
import com.ccqstark.qrpc.core.registry.ServiceDiscovery;
import com.ccqstark.qrpc.core.registry.zk.util.CuratorUtils;
import com.ccqstark.qrpc.core.remoting.dto.RpcRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author ccqstark
 * @description zookeeper服务发现实现
 * @date 2022/6/29 23:41 
 */
@Slf4j
public class ZkServiceDiscoveryImpl implements ServiceDiscovery {

    private final LoadBalance loadBalance;

    public ZkServiceDiscoveryImpl() {
        this.loadBalance = ExtensionLoader.getExtensionLoader(LoadBalance.class)
                .getExtension("loadBalance");
    }

    @Override
    public InetSocketAddress lookupService(RpcRequest rpcRequest) {
        String rpcServiceName = rpcRequest.getRpcServiceName();
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        List<String> serviceUrlList = CuratorUtils.getChildrenNodes(zkClient, rpcServiceName);
        if (CollectUtil.isEmpty(serviceUrlList)) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND, rpcServiceName);
        }
        // 负载均衡
        String targetServiceUrl = loadBalance.selectServiceAddress(serviceUrlList, rpcRequest);
        log.info("Successfully found the service address:[{}]", targetServiceUrl);
        String[] socketAddressArray = targetServiceUrl.split(":");
        String host = socketAddressArray[0];
        int post = Integer.parseInt(socketAddressArray[1]);
        return new InetSocketAddress(host, post);
    }
}
