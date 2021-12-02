package com.github.zou.rpc.common.domain.message;


import java.io.Serializable;

/**
 * 通知消息头
 * @author zou
 * @since 1.0.0
 */
public interface NotifyMessageHeader extends Serializable {

    /**
     * 消息类型
     * @return 消息类型
     * @since 1.0.0
     * @see MessageTypeConst 类型常量
     */
    String type();

}
