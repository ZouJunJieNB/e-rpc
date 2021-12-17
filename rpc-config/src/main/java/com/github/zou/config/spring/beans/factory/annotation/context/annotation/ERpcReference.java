package com.github.zou.config.spring.beans.factory.annotation.context.annotation;

import com.github.zou.rpc.client.support.fail.enums.FailTypeEnum;
import com.github.zou.rpc.common.constant.enums.CallTypeEnum;

import java.lang.annotation.*;

import static com.github.zou.rpc.common.constant.CommonsConst.defaultTimeout;

/**
 * ERPCReference
 * @author zou
 * @since 1.0.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface ERpcReference {

    /**
     * 接口的class，默认void的class.暂时不作用
     */
    Class<?> interfaceClass() default void.class;

    /**
     * 服务名称
     */
    String serverId() default "";

    /**
     * 超时时间
     * @return
     */
    long timeout() default defaultTimeout;

    /**
     * 调用方式
     * @return
     */
    CallTypeEnum callType() default CallTypeEnum.SYNC;

    /**
     * 失败类型
     * @return
     */
    FailTypeEnum failType() default FailTypeEnum.FAIL_OVER;


}
