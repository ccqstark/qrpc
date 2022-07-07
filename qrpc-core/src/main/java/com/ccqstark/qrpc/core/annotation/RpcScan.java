package com.ccqstark.qrpc.core.annotation;

import com.ccqstark.qrpc.core.spring.CustomScannerRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author ccqstark
 * @description 自定义扫描注解
 * @date 2022/7/6 15:35 
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Import(CustomScannerRegistrar.class)
@Documented
public @interface RpcScan {

    String[] basePackages();

}
