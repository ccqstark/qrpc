package com.ccqstark.qrpc.core.remoting.dto;

import lombok.*;

import java.io.Serializable;

/**
 * @author ccqstark
 * @description RPC消息体
 * @date 2022/6/5 16:10
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString
public class RpcMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private byte messageType;

    private byte codec;

    private byte compress;

    private Integer requestId;

    private Object data;

}
