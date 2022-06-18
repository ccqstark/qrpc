package com.ccqstark.qrpc.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author ccqstark
 * @description 压缩类型
 * @date 2022/6/14 01:15 
 */
@AllArgsConstructor
@Getter
public enum CompressTypeEnum {

    /**
     * gzip, 0x01是两位16进制数，16进制一位是4bit，所以这里刚好8bit也就是1个字节
     */
    GZIP((byte)0x01, "gzip");

    private final byte code;
    private final String name;

    /**
     * 根据输入的code获取对应的name
     */
    public static String getName(byte code) {
        for (CompressTypeEnum c : CompressTypeEnum.values()) {
            if (c.getCode() == code) {
                return  c.name;
            }
        }
        return null;
    }

}
