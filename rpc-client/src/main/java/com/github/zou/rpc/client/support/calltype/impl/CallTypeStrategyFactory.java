package com.github.zou.rpc.client.support.calltype.impl;


import com.github.zou.rpc.client.support.calltype.CallTypeStrategy;
import com.github.zou.rpc.common.constant.enums.CallTypeEnum;

/**
 * callType 策略工厂类
 * @author zou
 * @since 0.1.0
 */
public final class CallTypeStrategyFactory  {

    private CallTypeStrategyFactory(){}

    /**
     * 获取调用策略
     * @param callTypeEnum 调用类型枚举
     * @return 调用策略实现
     * @since 0.1.0
     */
    public static CallTypeStrategy callTypeStrategy(final CallTypeEnum callTypeEnum) {
        switch (callTypeEnum) {
            case SYNC:
                return SyncCallTypeStrategy.getInstance();
            case ONE_WAY:
                return OneWayCallTypeStrategy.getInstance();
            default:
                throw new UnsupportedOperationException("Not support call type : " + callTypeEnum);
        }
    }

}
