package io.github.ccqstark.qrpc.core.spring;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.annotation.Annotation;

/**
 * @author ccqstark
 * @description 自定义包扫描器
 * @date 2022/8/11 13:11
 */
public class CustomScanner extends ClassPathBeanDefinitionScanner {

    public CustomScanner(BeanDefinitionRegistry registry, Class<? extends Annotation> annoType) {
        // BeanDefinitionRegistry该类的作用主要是向注册表中注册BeanDefinition实例完成注册的过程
        super(registry);
        // 添加扫描过滤条件，被指定注解annoType修饰的类也会生成bean注入容器
        super.addIncludeFilter(new AnnotationTypeFilter(annoType));
    }

    @Override
    public int scan(String ... basePackages) {
        return super.scan(basePackages);
    }

}
