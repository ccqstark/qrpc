package com.ccqstark.qrpc.core.remoting.serialize.kryo;

import com.ccqstark.qrpc.common.exception.SerializeException;
import com.ccqstark.qrpc.core.remoting.dto.RpcRequest;
import com.ccqstark.qrpc.core.remoting.dto.RpcResponse;
import com.ccqstark.qrpc.core.remoting.serialize.Serializer;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @author ccqstark
 * @description kryo的序列化实现
 * @date 2022/6/5 16:39
 */
public class KryoSerializer implements Serializer {

    /**
     * kryo线程不安全, 每当需要序列化和反序列化时都需要实例化一次，或者借助ThreadLocal来维护以保证其线程安全
     */
    private final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.register(RpcRequest.class);
        kryo.register(RpcResponse.class);
        return kryo;
    });

    @Override
    public byte[] serialize(Object obj) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             Output output = new Output(byteArrayOutputStream)) {
            Kryo kryo = kryoThreadLocal.get();
            kryo.writeObject(output, obj);
            kryoThreadLocal.remove();
            return output.toBytes();
        } catch (Exception e) {
            throw new SerializeException("Serialization failed");
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             Input input = new Input(byteArrayInputStream)) {
            Kryo kryo = kryoThreadLocal.get();
            Object obj = kryo.readObject(input, clazz);
            kryoThreadLocal.remove();
            return clazz.cast(obj);
        } catch (Exception e) {
            throw new SerializeException("Serialization failed");
        }
    }

}
