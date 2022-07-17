package io.github.ccqstark.qrpc.core.loadbalance;

import io.github.ccqstark.qrpc.common.utils.CollectUtil;
import io.github.ccqstark.qrpc.core.remoting.dto.RpcRequest;

import java.util.List;

/**
 * @author ccqstark
 * @description 负载均衡策略抽象类
 * @date 2022/6/27 23:49
 */
public abstract class AbstractLoadBalance implements LoadBalance {

    @Override
    public String selectServiceAddress(List<String> serviceAddresses, RpcRequest rpcRequest) {
        if (CollectUtil.isEmpty(serviceAddresses)) {
            return null;
        }
        if (serviceAddresses.size() == 1) {
            return serviceAddresses.get(0);
        }
        return doSelect(serviceAddresses, rpcRequest);
    }

    protected abstract String doSelect(List<String> serviceAddresses, RpcRequest rpcRequest);

}
