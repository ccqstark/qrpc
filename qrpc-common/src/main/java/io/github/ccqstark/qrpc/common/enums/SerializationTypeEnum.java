package io.github.ccqstark.qrpc.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author ccqstark
 * @description 序列化类型
 * @date 2022/6/14 01:34
 */
@AllArgsConstructor
@Getter
public enum SerializationTypeEnum {

    /**
     * kryo
     */
    KRYO((byte) 0x01, "kryo"),
    /**
     * protostuff
     */
    PROTOSTUFF((byte) 0x02, "protostuff"),
    /**
     * hessian
     */
    HESSIAN((byte) 0x03, "hessian");

    private final byte code;
    private final String name;

    /**
     * 根据输入的code获取对应的name
     */
    public static String getName(byte code) {
        for (SerializationTypeEnum c : SerializationTypeEnum.values()) {
            if (c.getCode() == code) {
                return  c.name;
            }
        }
        return null;
    }

}
