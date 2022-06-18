package com.ccqstark.qrpc.core.remoting.transport;

import com.ccqstark.qrpc.common.extension.SPI;
import com.ccqstark.qrpc.core.remoting.dto.RpcRequest;

/**
 * @author ccqstark
 * @description 网络请求传输接口
 * @date 2022/6/5 17:56
 */
@SPI
public interface RpcRequestTransport {

    /**
     * 发送Rpc请求体并接受服务端返回
     *
     * @param rpcRequest 请求体
     * @return 服务端返回数据
     */
    Object sendRpcRequest(RpcRequest rpcRequest);

}
