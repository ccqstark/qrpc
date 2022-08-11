package io.github.ccqstark.qrpc.core.remoting.dto;

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

    /**
     * 请求 id
     */
    private String requestId;

    /**
     * 接口名
     */
    private String interfaceName;

    /**
     * 方法名
     */
    private String methodName;

    /**
     * 参数列表
     */
    private Object[] parameters;

    /**
     * 参数类型列表
     */
    private Class<?>[] parameterTypes;

    /**
     * 版本号
     */
    private String version;

    /**
     * 分组
     */
    private String group;

    public String getRpcServiceName() {
        return interfaceName + group + version;
    }

}
