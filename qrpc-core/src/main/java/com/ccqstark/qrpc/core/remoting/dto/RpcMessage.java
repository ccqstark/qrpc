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
public class RpcMessage {

    private byte messageType;

    private byte codec;

    private byte compress;

    private int requestId;

    private Object data;

}
