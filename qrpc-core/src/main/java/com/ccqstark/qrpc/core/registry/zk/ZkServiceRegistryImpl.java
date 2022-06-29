package com.ccqstark.qrpc.core.registry.zk;

import com.ccqstark.qrpc.core.registry.ServiceRegistry;
import com.ccqstark.qrpc.core.registry.zk.util.CuratorUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;

/**
 * @author ccqstark
 * @description 基于zk的注册服务实现
 * @date 2022/6/20 00:46
 */
@Slf4j
public class ZkServiceRegistryImpl implements ServiceRegistry {
    @Override
    public void registerService(String serviceName, InetSocketAddress inetSocketAddress) {
        String servicePath = CuratorUtils.ZK_REGISTER_ROOT_PATH + "/" + serviceName + "/" + inetSocketAddress.toString();
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        CuratorUtils.createPersistentNode(zkClient, servicePath);
    }
}
