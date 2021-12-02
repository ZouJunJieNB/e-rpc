package com.github.zou.rpc.common.domain.message;


import com.github.zou.rpc.common.rpc.domain.BaseRpc;

/**
 * 注册消息体
 * @author zou
 * @since 1.0.0
 */
public interface NotifyMessage extends BaseRpc {

    /**
     * 头信息
     * @return 头信息
     */
    NotifyMessageHeader header();

    /**
     * 消息信息体
     * @return 消息信息体
     */
    Object body();

}
