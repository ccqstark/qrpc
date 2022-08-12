package io.github.ccqstark.qrpc.core.config;

import io.github.ccqstark.qrpc.common.utils.concurrent.ThreadPoolFactoryUtil;
import io.github.ccqstark.qrpc.core.registry.zk.util.CuratorUtils;
import io.github.ccqstark.qrpc.core.remoting.transport.netty.server.NettyRpcServer;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * @author ccqstark
 * @description JVM关闭钩子，实现程序的平滑退出（优雅退出）
 * @date 2022/8/12 12:15
 */
@Slf4j
public class CustomShutdownHook {

    private static final CustomShutdownHook CUSTOM_SHUTDOWN_HOOK = new CustomShutdownHook();

    public static CustomShutdownHook getCustomShutdownHook() {
        return CUSTOM_SHUTDOWN_HOOK;
    }

    public void clearAll() {
        log.info("add ShutdownHook for clearAll");
        // 添加JVM关闭钩子
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                // 移除注册中心对应的服务地址
                InetSocketAddress inetSocketAddress = new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), NettyRpcServer.PORT);
                CuratorUtils.clearRegistry(CuratorUtils.getZkClient(), inetSocketAddress);
            } catch (UnknownHostException ignored) {
            }
            // 关闭线程池
            ThreadPoolFactoryUtil.shutDownThreadPool();
        }));
    }
}
