package com.github.zou.rpc.common.domain.message.impl;


import com.github.zou.rpc.common.domain.message.NotifyMessage;
import com.github.zou.rpc.common.domain.message.NotifyMessageHeader;

/**
 * 默认注册消息
 * @author zou
 * @since 1.0.0
 */
class DefaultNotifyMessage implements NotifyMessage {

    private static final long serialVersionUID = 3979588494064088927L;

    /**
     * 唯一序列号标识
     */
    private String seqId;

    /**
     * 头信息
     */
    private NotifyMessageHeader header;

    /**
     * 消息信息体
     */
    private Object body;

    @Override
    public String seqId() {
        return seqId;
    }

    @Override
    public DefaultNotifyMessage seqId(String seqId) {
        this.seqId = seqId;
        return this;
    }

    @Override
    public NotifyMessageHeader header() {
        return header;
    }

    public DefaultNotifyMessage header(NotifyMessageHeader header) {
        this.header = header;
        return this;
    }

    @Override
    public Object body() {
        return body;
    }

    public DefaultNotifyMessage body(Object body) {
        this.body = body;
        return this;
    }

    @Override
    public String toString() {
        return "DefaultRegisterMessage{" +
                "header=" + header +
                ", body=" + body +
                '}';
    }

}
