package com.ccqstark.qrpc.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author ccqstark
 * @description RPC响应状态码
 * @date 2022/6/5 15:44 
 */
@AllArgsConstructor
@Getter
@ToString
public enum RpcResponseCodeEnum {

    /**
     * 成功
     */
    SUCCESS(200, "The remote call is successful"),

    /**
     * 失败
     */
    FAIL(500, "The remote call is fail")
    ;

    private final Integer code;

    private final String message;

}
