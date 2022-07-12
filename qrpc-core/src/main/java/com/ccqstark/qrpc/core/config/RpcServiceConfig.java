package com.ccqstark.qrpc.core.config;

import lombok.*;

/**
 * @author ccqstark
 * @description 服务配置
 * @date 2022/7/7 01:05
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RpcServiceConfig {

    /**
     * 服务版本
     */
    private String version = "";

    /**
     * 当接口有多个实现类，按这个group来区分
     */
    private String group = "";

    /**
     * 目标服务
     */
    private Object service;

    public String getRpcServiceName() {
        return this.getServiceName() + this.getGroup() + this.getVersion();
    }

    public String getServiceName() {
        return this.service.getClass().getInterfaces()[0].getCanonicalName();
    }

}
