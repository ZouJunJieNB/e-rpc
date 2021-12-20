package com.github.zou.config.spring.beans.factory.annotation.context.annotation;

import java.lang.annotation.*;

/**
 * @author zou
 * @since 1.0.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface ERpcService {

    /**
     * 服务名称，默认取类接口名
     * @return 值
     */
    String serverId() default "";

}
