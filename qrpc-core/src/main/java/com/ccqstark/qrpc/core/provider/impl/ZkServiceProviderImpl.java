package com.ccqstark.qrpc.core.provider.impl;

import com.ccqstark.qrpc.common.enums.RpcErrorMessageEnum;
import com.ccqstark.qrpc.common.exception.RpcException;
import com.ccqstark.qrpc.common.extension.ExtensionLoader;
import com.ccqstark.qrpc.core.config.RpcServiceConfig;
import com.ccqstark.qrpc.core.provider.ServiceProvider;
import com.ccqstark.qrpc.core.registry.ServiceRegistry;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ZkServiceProviderImpl implements ServiceProvider {

    /**
     * key: rpc service name(interface name + version + group)
     * value: service object
     */
    private final Map<Object, Object> serviceMap;
    private final Set<String> registeredService;
    private final ServiceRegistry serviceRegistry;


    public ZkServiceProviderImpl() {
        this.serviceMap = new ConcurrentHashMap<>();
        this.registeredService = ConcurrentHashMap.newKeySet();
        this.serviceRegistry = ExtensionLoader.getExtensionLoader(ServiceRegistry.class).getExtension("zk");
    }

    @Override
    public void addService(RpcServiceConfig rpcServiceConfig) {
        String rpcServiceName = rpcServiceConfig.getRpcServiceName();
        if (registeredService.contains(rpcServiceName)) {
            return;
        }
        registeredService.add(rpcServiceName);
        serviceMap.put(rpcServiceName, rpcServiceConfig.getService());
        log.info("Add service: {} and interfaces: {}", rpcServiceName, rpcServiceConfig.getService().getClass().getInterfaces());
    }

    @Override
    public Object getServiceObject(String rpcServiceName) {
        Object service = serviceMap.get(rpcServiceName);
        if (null == service) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND);
        }
        return service;
    }

    @Override
    public void publishService(RpcServiceConfig rpcServiceConfig) {
        try {
            String host = InetAddress.getLocalHost().getHostAddress();
            this.addService(rpcServiceConfig);
            serviceRegistry.registerService(rpcServiceConfig.getServiceName(), new InetSocketAddress(host, ));
        } catch (UnknownHostException e) {
            log.error("occur exception when getHostAddress", e);
        }
    }
}
