package io.github.ccqstark.qrpc.core.remoting.transport.netty.client;

import io.github.ccqstark.qrpc.core.remoting.dto.RpcResponse;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ccqstark
 * @description 未被服务端处理的请求
 * @date 2022/7/11 01:24
 */
public class UnprocessedRequests {

    // 用于存储未得到响应的请求对应的future
    public static final Map<String, CompletableFuture<RpcResponse<Object>>> UNPROCESSED_RESPONSE_FUTURES = new ConcurrentHashMap<>();

    public void put(String requestId, CompletableFuture<RpcResponse<Object>> future) {
        UNPROCESSED_RESPONSE_FUTURES.put(requestId, future);
    }

    /**
     * 接受响应数据，放进CompletableFuture中
     */
    public void complete(RpcResponse<Object> rpcResponse) {
        // 从未完成的请求的中移除
        CompletableFuture<RpcResponse<Object>> future = UNPROCESSED_RESPONSE_FUTURES.remove(rpcResponse.getRequestId());
        if (null != future) {
            future.complete(rpcResponse);
        } else {
            throw new IllegalStateException();
        }
    }

}
