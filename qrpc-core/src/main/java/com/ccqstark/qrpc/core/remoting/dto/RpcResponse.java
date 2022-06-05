package com.ccqstark.qrpc.core.remoting.dto;

import com.ccqstark.qrpc.common.enums.RpcResponseCodeEnum;
import lombok.*;

import java.io.Serializable;

/**
 * @author ccqstark
 * @description RPC响应体
 * @date 2022/6/5 15:35
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString
public class RpcResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private String requestId;

    private Integer code;

    private String message;

    private T data;

    /**
     * 成功响应
     *
     * @param data      返回数据体
     * @param requestId 请求id
     * @return 响应体
     */
    public static <T> RpcResponse<T> success(T data, String requestId) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setCode(RpcResponseCodeEnum.SUCCESS.getCode());
        response.setMessage(RpcResponseCodeEnum.SUCCESS.getMessage());
        response.setRequestId(requestId);
        if (data != null) {
            response.setData(data);
        }
        return response;
    }

    /**
     * 失败响应
     *
     * @param rpcResponseCodeEnum RPC响应状态码
     * @return 响应体
     */
    public static <T> RpcResponse<T> fail(RpcResponseCodeEnum rpcResponseCodeEnum) {
        RpcResponse<T> response = new RpcResponse<T>();
        response.setCode(rpcResponseCodeEnum.getCode());
        response.setMessage(rpcResponseCodeEnum.getMessage());
        return response;
    }

}
