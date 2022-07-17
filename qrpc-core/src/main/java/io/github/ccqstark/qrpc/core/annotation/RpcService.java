package io.github.ccqstark.qrpc.core.annotation;

import java.lang.annotation.*;

/**
 * @author ccqstark
 * @description Rpc服务注解
 * @date 2022/7/6 23:04
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface RpcService {

    /**
     * 服务版本，默认为空
     */
    String version() default "";

    /**
     * 服务组，默认为空
     */
    String group() default "";

}
