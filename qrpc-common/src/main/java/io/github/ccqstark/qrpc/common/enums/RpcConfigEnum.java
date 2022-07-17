package io.github.ccqstark.qrpc.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author ccqstark
 * @description 配置信息
 * @date 2022/6/20 00:08
 */
@AllArgsConstructor
@Getter
public enum RpcConfigEnum {

    /**
     * 配置文件路径
     */
    RPC_CONFIG_PATH("qrpc.properties"),

    /**
     * zookeeper地址
     */
    ZK_ADDRESS("qrpc.zookeeper.address");

    private final String propertyValue;

}
