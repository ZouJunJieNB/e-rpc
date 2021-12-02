package com.github.zou.rpc.client.support.fail.impl;

import com.github.houbb.heaven.annotation.ThreadSafe;
import com.github.zou.rpc.client.proxy.RemoteInvokeContext;
import com.github.zou.rpc.client.support.fail.FailStrategy;
import com.github.zou.rpc.common.rpc.domain.impl.RpcResponses;

/**
 * 快速失败策略
 * @author zou
 * @since 1.0.0
 */
@ThreadSafe
class FailFastStrategy implements FailStrategy {

    @Override
    public Object fail(final RemoteInvokeContext context) {
        final Class returnType = context.request().returnType();
        return RpcResponses.getResult(context.rpcResponse(), returnType);
    }

}
