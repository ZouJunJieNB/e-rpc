package com.github.zou.rpc.common.rpc.domain;

/**
 * 序列化相关处理
 * @author zou
 * @since 1.0.0
 */
public interface RpcResponse extends BaseRpc {

    /**
     * 异常信息
     * @return 异常信息
     */
    Throwable error();

    /**
     * 请求结果
     * @return 请求结果
     */
    Object result();

}
