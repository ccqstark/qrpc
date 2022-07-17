package io.github.ccqstark.qrpc.core.loadbalance.loadbalancer;

import io.github.ccqstark.qrpc.core.loadbalance.AbstractLoadBalance;
import io.github.ccqstark.qrpc.core.remoting.dto.RpcRequest;

import java.util.List;
import java.util.Random;

/**
 * @author ccqstark
 * @description 负载均衡：随机算法
 * @date 2022/6/27 23:51
 */
public class RandomLoadBalance extends AbstractLoadBalance {
    @Override
    protected String doSelect(List<String> serviceAddresses, RpcRequest rpcRequest) {
        Random random = new Random();
        return serviceAddresses.get(random.nextInt(serviceAddresses.size()));
    }
}
