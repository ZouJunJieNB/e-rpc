package com.github.zou.rpc.client.support.filter.impl;


import com.github.houbb.heaven.annotation.ThreadSafe;
import com.github.houbb.heaven.support.pipeline.Pipeline;
import com.github.houbb.heaven.support.pipeline.impl.DefaultPipeline;
import com.github.zou.rpc.client.proxy.RemoteInvokeContext;
import com.github.zou.rpc.client.support.filter.RpcFilter;

import java.util.List;

/**
 * 抽象的过滤器初始化
 * @author zou
 * @since 1.0.0
 */
@ThreadSafe
public abstract class AbstractRpcFilterInit implements RpcFilter {

    /**
     * 初始化监听器列表
     * @param pipeline 泳道
     * @param context 重试信息
     * @since 0.0.7
     */
    protected abstract void init(final Pipeline<RpcFilter> pipeline,
                       final RemoteInvokeContext context);

    @Override
    public void filter(RemoteInvokeContext conditionContext) {
        Pipeline<RpcFilter> pipeline = new DefaultPipeline<>();
        this.init(pipeline, conditionContext);

        List<RpcFilter> filterList = pipeline.list();

        for(RpcFilter filter : filterList) {
            filter.filter(conditionContext);
        }
    }

}
