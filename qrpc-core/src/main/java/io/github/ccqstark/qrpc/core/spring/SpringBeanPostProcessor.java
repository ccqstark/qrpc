package io.github.ccqstark.qrpc.core.spring;

import io.github.ccqstark.qrpc.common.extension.ExtensionLoader;
import io.github.ccqstark.qrpc.common.factory.SingletonFactory;
import io.github.ccqstark.qrpc.core.annotation.RpcReference;
import io.github.ccqstark.qrpc.core.annotation.RpcService;
import io.github.ccqstark.qrpc.core.config.RpcServiceConfig;
import io.github.ccqstark.qrpc.core.provider.ServiceProvider;
import io.github.ccqstark.qrpc.core.provider.impl.ZkServiceProviderImpl;
import io.github.ccqstark.qrpc.core.proxy.RpcClientProxy;
import io.github.ccqstark.qrpc.core.remoting.transport.RpcRequestTransport;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * @author ccqstark
 * @description 在bean创建后对自定义注解进行处理
 * @date 2022/8/11 10:39
 */
@Slf4j
@Component
public class SpringBeanPostProcessor implements BeanPostProcessor {

    private final ServiceProvider serviceProvider;
    private final RpcRequestTransport rpcClient;

    public SpringBeanPostProcessor() {
        this.serviceProvider = SingletonFactory.getInstance(ZkServiceProviderImpl.class);
        this.rpcClient = ExtensionLoader.getExtensionLoader(RpcRequestTransport.class).getExtension("netty");
    }

    @SneakyThrows
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        // 判断是否带有@RpcService注解
        if (bean.getClass().isAnnotationPresent(RpcService.class)) {
            log.info("[{}] is annotated with [{}]", bean.getClass().getName(), RpcService.class.getCanonicalName());
            // 获取RpcService注解里的信息
            RpcService rpcService = bean.getClass().getAnnotation(RpcService.class);
            // 发布Rpc服务
            RpcServiceConfig rpcServiceConfig = RpcServiceConfig.builder()
                    .group(rpcService.group())
                    .version(rpcService.version())
                    .service(bean).build();
            serviceProvider.publishService(rpcServiceConfig);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // 获取目标类中的所有字段
        Class<?> targetClass = bean.getClass();
        Field[] declaredFields = targetClass.getDeclaredFields();
        // 循环目标类中所有的字段，判断是否含有 @RpcReference 注解
        for (Field declaredField : declaredFields) {
            RpcReference rpcReference = declaredField.getAnnotation(RpcReference.class);
            if (rpcReference != null) {
                RpcServiceConfig rpcServiceConfig = RpcServiceConfig.builder()
                        .group(rpcReference.group())
                        .version(rpcReference.version()).build();
                // 生成代理对象
                RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcClient, rpcServiceConfig);
                Object clientProxy = rpcClientProxy.getProxy(declaredField.getType());
                declaredField.setAccessible(true);
                try {
                    // 注入bean的其实是代理对象
                    declaredField.set(bean, clientProxy);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return bean;
    }

}
