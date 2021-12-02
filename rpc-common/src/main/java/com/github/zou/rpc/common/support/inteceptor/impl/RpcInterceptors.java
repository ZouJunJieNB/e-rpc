package com.github.zou.rpc.common.support.inteceptor.impl;

import com.github.houbb.heaven.support.pipeline.Pipeline;
import com.github.houbb.heaven.util.lang.ObjectUtil;
import com.github.houbb.heaven.util.util.ArrayUtil;
import com.github.zou.rpc.common.support.inteceptor.RpcInterceptor;
import com.github.zou.rpc.common.support.inteceptor.RpcInterceptorContext;

/**
 * rpc 拦截器适配器
 * @author zou
 * @since 1.0.0
 */
public final class RpcInterceptors {

    private RpcInterceptors(){}

    /**
     * 什么都不做的拦截器
     * @return 实现
     * @since 0.2.2
     */
    public static RpcInterceptor none() {
        return new NoneRpcInterceptor();
    }

    /**
     * 耗时拦截器
     * @return 实现
     * @since 0.2.2
     */
    public static RpcInterceptor costTime() {
        return new CostTimeRpcInterceptor();
    }

    /**
     * 日志拦截器
     * @return 实现
     * @since 0.2.2
     */
    public static RpcInterceptor log() {
        return new LogRpcInterceptor();
    }

    /**
     * 生成对应的过滤器链
     * @param first 第一个
     * @param others 其他
     * @return 条件实现
     * @since 0.2.2
     */
    public static RpcInterceptor chains(final RpcInterceptor first, final RpcInterceptor... others) {
        return new AbstractRpcInterceptorInit() {
            @Override
            protected void init(Pipeline<RpcInterceptor> pipeline, RpcInterceptorContext context) {
                pipeline.addLast(first);

                if(ArrayUtil.isNotEmpty(others)) {
                    for(RpcInterceptor other : others) {
                        if(ObjectUtil.isNull(other)) {
                            continue;
                        }
                        pipeline.addLast(other);
                    }
                }
            }
        };
    }

}
