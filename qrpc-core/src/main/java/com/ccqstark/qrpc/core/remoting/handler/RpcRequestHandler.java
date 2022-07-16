package com.ccqstark.qrpc.core.remoting.handler;

import com.ccqstark.qrpc.common.factory.SingletonFactory;
import com.ccqstark.qrpc.core.provider.ServiceProvider;
import com.ccqstark.qrpc.core.provider.impl.ZkServiceProviderImpl;
import com.ccqstark.qrpc.core.remoting.dto.RpcRequest;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author ccqstark
 * @description rpc请求处理器
 * @date 2022/7/10 16:54
 */
@Slf4j
public class RpcRequestHandler {

    private final ServiceProvider serviceProvider;

    public RpcRequestHandler() {
        serviceProvider = SingletonFactory.getInstance(ZkServiceProviderImpl.class);
    }

    /**
     * 处理rpc请求：调用目标服务方法，返回响应结果
     */
    public Object handle(RpcRequest rpcRequest) {
        Object service = serviceProvider.getService(rpcRequest.getRpcServiceName());
        return invokeTargetMethod(rpcRequest, service);
    }

    /**
     * 获取方法执行结果
     *
     * @param rpcRequest 客户端请求
     * @param service    服务对象
     * @return 目标方法的执行结果
     */
    private Object invokeTargetMethod(RpcRequest rpcRequest, Object service) {
        Object result;
        try {
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
            result = method.invoke(service, rpcRequest.getParameters());
            log.info("service: [{}] successful invoke method: [{}]", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
        } catch (NoSuchMethodException | IllegalArgumentException | InvocationTargetException |
                 IllegalAccessException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return result;
    }

}
