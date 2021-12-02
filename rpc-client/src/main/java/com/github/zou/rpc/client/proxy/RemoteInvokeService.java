package com.github.zou.rpc.client.proxy;

/**
 * 远程调用服务
 * @author zou
 * @since 1.0.0
 */
public interface RemoteInvokeService {

    /**
     * 远程调用
     * @param context 远程调用上下文
     * @return 最终调用结果
     */
    Object remoteInvoke(final RemoteInvokeContext context);

}
