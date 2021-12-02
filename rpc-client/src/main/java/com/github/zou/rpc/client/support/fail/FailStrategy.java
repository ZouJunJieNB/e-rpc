package com.github.zou.rpc.client.support.fail;


import com.github.zou.rpc.client.proxy.RemoteInvokeContext;

/**
 * 失败策略
 * @author zou
 * @since 1.0.0
 */
public interface FailStrategy {

    /**
     * 失败策略
     * @param context 远程调用上下文
     * @return 最终的结果值
     * @since 0.1.1
     */
    Object fail(final RemoteInvokeContext context);

}
