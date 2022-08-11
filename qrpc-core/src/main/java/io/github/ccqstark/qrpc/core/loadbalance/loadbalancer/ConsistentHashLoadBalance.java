package io.github.ccqstark.qrpc.core.loadbalance.loadbalancer;

import io.github.ccqstark.qrpc.core.loadbalance.AbstractLoadBalance;
import io.github.ccqstark.qrpc.core.remoting.dto.RpcRequest;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ccqstark
 * @description 负载均衡：一致性哈希
 * @date 2022/6/29 22:47
 */
public class ConsistentHashLoadBalance extends AbstractLoadBalance {

    /**
     * 存储所有的服务节点，key为服务名称，value为对应服务的selector
     */
    private final ConcurrentHashMap<String, ConsistentHashSelector> selectors = new ConcurrentHashMap<>();

    /**
     * 一致性哈希的服务节点选择方法（实际被调用）
     *
     * @param serviceAddresses 服务地址列表
     * @param rpcRequest       rpc请求
     * @return
     */
    @Override
    protected String doSelect(List<String> serviceAddresses, RpcRequest rpcRequest) {
        // 为该服务节点地址生成一个身份标识码
        int identityHashCode = System.identityHashCode(serviceAddresses);
        // 获取rpc服务名称
        String rpcServerName = rpcRequest.getRpcServiceName();
        // 尝试从上面的map存储中获取selector
        ConsistentHashSelector selector = selectors.get(rpcServerName);
        // 检查是否更新过，利用identityHashCode
        if (selector == null || selector.identityHashCode != identityHashCode) {
            // 如果为空，或者发现已经更新，则重新初始化一个selector
            selectors.put(rpcServerName, new ConsistentHashSelector(serviceAddresses, 160, identityHashCode));
            selector = selectors.get(rpcServerName);
        }
        // 调用选择器获取服务实例地址，这里用服务名和参数生成的key，Dubbo 原实现中只与参数有关
        return selector.select(rpcServerName + Arrays.stream(rpcRequest.getParameters()));
    }

    /**
     * 封装的一致性哈希选择器
     */
    static class ConsistentHashSelector {
        /**
         * 虚拟节点（用TreeMap保证顺序性）
         */
        private final TreeMap<Long, String> virtualInvokers;
        /**
         * 身份标识码
         */
        private final int identityHashCode;

        ConsistentHashSelector(List<String> invokers, int replicaNumber, int identityHashCode) {
            this.virtualInvokers = new TreeMap<>();
            this.identityHashCode = identityHashCode;
            // 这里的invokers其实是该服务可用的实例地址
            for (String invoker : invokers) {
                for (int i = 0; i < replicaNumber / 4; i++) {
                    // 每个服务地址都生成replicaNumber个虚拟节点
                    byte[] digest = md5(invoker + i);
                    for (int h = 0; h < 4; h++) {
                        long m = hash(digest, h);
                        virtualInvokers.put(m, invoker);
                    }
                }
            }
        }

        /**
         * md5摘要算法
         */
        static byte[] md5(String key) {
            MessageDigest md;
            try {
                md = MessageDigest.getInstance("MD5");
                byte[] bytes = key.getBytes(StandardCharsets.UTF_8);
                md.update(bytes);
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }

            return md.digest();
        }

        /**
         * 哈希算法
         */
        static long hash(byte[] digest, int idx) {
            return ((long) (digest[3 + idx * 4] & 255) << 24 |
                    (long) (digest[2 + idx * 4] & 255) << 16 |
                    (long) (digest[1 + idx * 4] & 255) << 8 |
                    (long) (digest[idx * 4] & 255)) & 4294967295L;
        }

        /**
         * 根据服务的key选取要调用的服务地址
         *
         * @param rpcServiceKey 服务的key=服务名+参数生成的stream
         * @return 服务地址
         */
        public String select(String rpcServiceKey) {
            byte[] digest = md5(rpcServiceKey);
            return selectForKey(hash(digest, 0));
        }

        /**
         * 根据哈希码获取服务地址（哈希环中大于哈希码的最近一个服务节点）
         *
         * @param hashCode 哈希值
         * @return 服务地址
         */
        public String selectForKey(long hashCode) {
            // 返回大于当前hash值的第一个节点
            Map.Entry<Long, String> entry = virtualInvokers.ceilingEntry(hashCode);
            // 如果节点为null就返回整个map的第一个节点（回环）
            if (entry == null) {
                entry = virtualInvokers.firstEntry();
            }
            return entry.getValue();
        }
    }

}
