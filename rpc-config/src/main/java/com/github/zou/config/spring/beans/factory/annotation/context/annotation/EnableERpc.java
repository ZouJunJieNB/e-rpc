package com.github.zou.config.spring.beans.factory.annotation.context.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 将ERpc组件作为Spring bean启用，等于{@link ERpcComponentScan}和后期别的注解的组合<p>
 * 注意：{@link EnableERpc}必须基于Spring框架4.2及以上版本
 * @author zou
 * @since 1.0.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@ERpcComponentScan
public @interface EnableERpc {

    /**
     * 用于扫描带注释的{@link ERpcService}类的基本包
     * <p>
     * 使用{@link #scanBasePackageClasses（）}作为基于字符串的包名的类型安全替代方案。
     *
     * @return 要扫描的基本包
     * @see ERpcComponentScan#basePackages()
     */
    @AliasFor(annotation = ERpcComponentScan.class, attribute = "basePackages")
    String[] scanBasePackages() default {};

    /**
     * 键入{@link #scanBasePackages（）}的安全替代方法，用于指定要扫描的包以查找带注释的{@link ERpcService}类。将扫描每个指定类别的包。
     *
     * @return classes from the base packages to scan
     * @see ERpcComponentScan#basePackageClasses
     */
    @AliasFor(annotation = ERpcComponentScan.class, attribute = "basePackageClasses")
    Class<?>[] scanBasePackageClasses() default {};
}
