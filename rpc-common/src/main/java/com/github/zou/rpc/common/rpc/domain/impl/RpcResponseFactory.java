package com.github.zou.rpc.common.rpc.domain.impl;


import com.github.zou.rpc.common.exception.RpcTimeoutException;
import com.github.zou.rpc.common.exception.ShutdownException;
import com.github.zou.rpc.common.rpc.domain.RpcResponse;

/**
 * 响应工厂类
 * @author zou
 * @since 1.0.0
 */
public final class RpcResponseFactory {

    private RpcResponseFactory(){}

    /**
     * 超时异常信息
     * @since 0.0.7
     */
    private static final DefaultRpcResponse TIMEOUT;

    /**
     * 状态已经关闭
     * @since 0.1.3
     */
    private static final DefaultRpcResponse SHUTDOWN;

    /**
     * 打断异常
     * @since 0.1.3
     */
    private static final DefaultRpcResponse INTERRUPTED;

    static {
        TIMEOUT = new DefaultRpcResponse();
        TIMEOUT.error(new RpcTimeoutException());

        SHUTDOWN = new DefaultRpcResponse();
        SHUTDOWN.error(new ShutdownException());

        INTERRUPTED = new DefaultRpcResponse();
        INTERRUPTED.error(new InterruptedException());
    }

    /**
     * 获取超时响应结果
     * @return 响应结果
     * @since 0.0.7
     */
    public static RpcResponse timeout() {
        return TIMEOUT;
    }

    /**
     * 获取 shutdown 响应结果
     * @return 响应结果
     * @since 0.1.3
     */
    public static RpcResponse shutdown() {
        return SHUTDOWN;
    }

    /**
     * 获取 INTERRUPTED 响应结果
     * @return 响应结果
     * @since 0.1.3
     */
    public static RpcResponse interrupted() {
        return INTERRUPTED;
    }

}
