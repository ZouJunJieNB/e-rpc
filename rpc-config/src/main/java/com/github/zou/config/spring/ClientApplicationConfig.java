package com.github.zou.config.spring;

import com.github.zou.rpc.client.support.fail.enums.FailTypeEnum;
import com.github.zou.rpc.common.constant.enums.CallTypeEnum;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

import static com.github.zou.rpc.client.support.fail.enums.FailTypeEnum.FAIL_FAST;
import static com.github.zou.rpc.client.support.fail.enums.FailTypeEnum.FAIL_OVER;
import static com.github.zou.rpc.common.constant.enums.CallTypeEnum.*;

/**
 * @author zou
 * @since 1.0.0
 */
@ConfigurationProperties(value = "e-rpc.application.client")
public class ClientApplicationConfig {

    /**
     * 超时时间
     */
    private long timeout;

    /**
     * 调用方式
     */
    private String callType;

    /**
     * 失败类型
     */
    private String failType;

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public CallTypeEnum getCallType() {
        return callNameMaps.get(this.callType);
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public FailTypeEnum getFailType() {
        return failNameMaps.get(this.failType);
    }

    public void setFailType(String failType) {
        this.failType = failType;
    }



    public static String failFast = "failFast";
    public static String FailOver = "FailOver";

    private final static Map<String, FailTypeEnum> failNameMaps = new HashMap<>();
    static {
        failNameMaps.put(failFast,FAIL_FAST);
        failNameMaps.put(FailOver,FAIL_OVER);
    }


    public static String oneWay = "oneWay";
    public static String sync = "sync";
    public static String async = "async";
    public static String callback = "callback";

    private final static Map<String,CallTypeEnum> callNameMaps = new HashMap<>();
    static {
        callNameMaps.put(oneWay,ONE_WAY);
        callNameMaps.put(sync,SYNC);
        callNameMaps.put(async,ASYNC);
        callNameMaps.put(callback,CALLBACK);
    }

}
