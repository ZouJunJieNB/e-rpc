package com.github.zou.rpc.common.support.status.service.impl;


import com.github.houbb.heaven.annotation.ThreadSafe;
import com.github.zou.rpc.common.support.status.enums.StatusEnum;
import com.github.zou.rpc.common.support.status.service.StatusManager;

/**
 * 状态管理
 *
 * @author zou
 * @since 0.1.3
 */
@ThreadSafe
public class DefaultStatusManager implements StatusManager {

    /**
     * 状态
     * @since 0.1.3
     */
    private volatile int status = StatusEnum.INIT.code();

    @Override
    public int status() {
        return status;
    }

    @Override
    public DefaultStatusManager status(int status) {
        this.status = status;
        return this;
    }

    @Override
    public String toString() {
        return "DefaultStatusManager{" +
                "status=" + status +
                '}';
    }

}
