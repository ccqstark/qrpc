package com.ccqstark.qrpc.common.utils;

import java.util.Collection;

/**
 * @author ccqstark
 * @description 集合工具类
 * @date 2022/6/27 23:46
 */
public class CollectUtil {
    public static boolean isEmpty(Collection<?> c) {
        return c == null || c.isEmpty();
    }
}
