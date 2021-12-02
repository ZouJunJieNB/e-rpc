package com.github.zou.rpc.client.support.fail.impl;

import com.github.houbb.heaven.annotation.ThreadSafe;
import com.github.zou.rpc.client.proxy.RemoteInvokeContext;
import com.github.zou.rpc.client.support.fail.FailStrategy;
import com.github.zou.rpc.common.exception.RpcRuntimeException;
import com.github.zou.rpc.common.exception.RpcTimeoutException;
import com.github.zou.rpc.common.rpc.domain.RpcResponse;
import com.github.zou.rpc.common.rpc.domain.impl.RpcResponses;

/**
 * 如果调用遇到异常，则进行尝试其他 server 端进行调用。
 * （1）最大重试次数=2  不能太多次
 * （2）重试的时候如何标识重试次数还剩多少次？
 * （3）如何在失败的时候获取重试相关上下文？
 *
 * @author zou
 * @since 1.0.0
 */
@ThreadSafe
class FailOverStrategy implements FailStrategy {

    @Override
    public Object fail(final RemoteInvokeContext context) {
        try {
            final Class returnType = context.request().returnType();
            final RpcResponse rpcResponse = context.rpcResponse();
            return RpcResponses.getResult(rpcResponse, returnType);
        } catch (Exception e) {
            Throwable throwable = e.getCause();
            if(throwable instanceof RpcTimeoutException) {
                throw new RpcRuntimeException();
            }

            // 进行失败重试。
            int retryTimes = context.retryTimes();
            if(retryTimes > 0) {
                // 进行重试
                retryTimes--;
                context.retryTimes(retryTimes);
                return context.remoteInvokeService()
                        .remoteInvoke(context);
            } else {
                throw e;
            }
        }
    }

}
