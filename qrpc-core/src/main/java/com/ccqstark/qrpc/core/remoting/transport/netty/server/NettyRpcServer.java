package com.ccqstark.qrpc.core.remoting.transport.netty.server;

import com.ccqstark.qrpc.common.factory.SingletonFactory;
import com.ccqstark.qrpc.core.config.RpcServiceConfig;
import com.ccqstark.qrpc.core.provider.ServiceProvider;
import com.ccqstark.qrpc.core.provider.impl.ZkServiceProviderImpl;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NettyRpcServer {

    public static final int PORT  = 9998;

    private final ServiceProvider serviceProvider = SingletonFactory.getInstance(ZkServiceProviderImpl.class);

    public void registerService(RpcServiceConfig rpcServiceConfig) {
        serviceProvider.publishService(rpcServiceConfig);
    }

    @SneakyThrows
    public void start() {
        
    }



}
