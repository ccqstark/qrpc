package com.ccqstark.qrpc.common.utils;

/**
 * @author ccqstark
 * @description 运行时工具类
 * @date 2022/7/9 18:13
 */
public class RuntimeUtil {
    /**
     * 获取CPU核心数
     */
    public static int cpus() {
        return Runtime.getRuntime().availableProcessors();
    }
}
