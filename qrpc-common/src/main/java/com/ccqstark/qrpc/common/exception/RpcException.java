package com.ccqstark.qrpc.common.exception;

import com.ccqstark.qrpc.common.enums.RpcErrorMessageEnum;

/**
 * @author ccqstark
 * @description rpc异常
 * @date 2022/6/29 23:37
 */
public class RpcException extends RuntimeException{
    public RpcException(RpcErrorMessageEnum rpcErrorMessageEnum, String detail) {
        super(rpcErrorMessageEnum.getMessage() + ":" + detail);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(RpcErrorMessageEnum rpcErrorMessageEnum) {
        super(rpcErrorMessageEnum.getMessage());
    }
}
