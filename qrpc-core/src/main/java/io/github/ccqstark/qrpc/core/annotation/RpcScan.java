package io.github.ccqstark.qrpc.core.annotation;

import io.github.ccqstark.qrpc.core.spring.CustomScannerRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author ccqstark
 * @description 自定义扫描注解
 * @date 2022/7/6 15:35 
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
// @Import通过快速导入的方式实现把实例加入spring的IOC容器中
@Import(CustomScannerRegistrar.class)
@Documented
public @interface RpcScan {

    String[] basePackage();

}
