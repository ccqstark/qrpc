package com.ccqstark.qrpc.core.serialize;

import com.ccqstark.qrpc.common.extension.SPI;

/**
 * @author ccqstark
 * @description 序列化接口
 * @date 2022/6/5 16:34
 */
@SPI
public interface Serializer {

    /**
     * 序列化
     *
     * @param obj 要序列化的对象
     * @return 序列化后的字节数组
     */
    byte[] serialize(Object obj);

    /**
     * 反序列化
     *
     * @param bytes 字节数组
     * @param clazz 反序列化后的类型
     * @return 反序列化后的对象
     */
    <T> T deserialize(byte[] bytes, Class<T> clazz);

}
