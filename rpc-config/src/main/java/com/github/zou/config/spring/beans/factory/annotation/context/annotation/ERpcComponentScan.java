package com.github.zou.config.spring.beans.factory.annotation.context.annotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author zou
 * @since 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(ERpcComponentScanRegistrar.class)
public @interface ERpcComponentScan {


    /**
     * basePackages（）属性的别名。允许更简洁的注释声明，
     * 例如：@ERpcComponentScan（“org.my.pkg”）而不是@ERpcComponentScan（basePackages=“org.my.pkg”）。
     *
     * @return 要扫描的基本包
     */
    String[] value() default {};

    /**
     * 用于扫描带注释的{@link ERpcService}类的基本包。value（）是此属性的别名（与此属性互斥）。
     * 使用basePackageClasses（）作为基于字符串的包名称的类型安全替代方案。
     *
     * @return 要扫描的基本包
     */
    String[] basePackages() default {};

    /**
     * 键入 #basePackages 的安全替代项，用于指定要扫描带注释的{@link ERpcService}类的包。将扫描每个指定类别的包。
     *
     * @return 要扫描的基本包中的类
     */
    Class<?>[] basePackageClasses() default {};
}
