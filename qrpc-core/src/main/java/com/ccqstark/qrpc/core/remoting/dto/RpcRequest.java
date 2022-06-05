package com.ccqstark.qrpc.core.remoting.dto;

import lombok.*;

import java.io.Serializable;

/**
 * @author ccqstark
 * @description RPC请求体
 * @date 2022/6/5 14:01
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString
public class RpcRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String requestId;

    private String interfaceName;

    private String methodName;

    private Object[] parameters;

    private Class<?>[] parameterTypes;

    private String version;

    private String group;

    public String getRpcServiceName() {
        return interfaceName + group + version;
    }

}
