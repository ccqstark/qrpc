package com.ccqstark.qrpc.common.utils;

/**
 * @author ccqstark
 * @description 字符串工具类
 * @date 2022/6/16 00:16
 */
public class StringUtil {

    /**
     * 判断是否为空白字符串
     * @param str 待判断字符串
     * @return  是否空白
     */
    public static boolean isBlank(String str) {
        if (str == null || str.length() == 0) {
            return true;
        }
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

}
