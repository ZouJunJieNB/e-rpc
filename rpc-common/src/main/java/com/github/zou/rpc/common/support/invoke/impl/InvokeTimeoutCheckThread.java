package com.github.zou.rpc.common.support.invoke.impl;


import com.github.houbb.heaven.util.common.ArgUtil;
import com.github.houbb.heaven.util.time.impl.Times;
import com.github.zou.rpc.common.rpc.domain.RpcResponse;
import com.github.zou.rpc.common.rpc.domain.impl.RpcResponseFactory;
import com.github.zou.rpc.common.support.invoke.InvokeManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 超时检测线程
 *
 * @author zou
 * @since 1.0.0
 */
public class InvokeTimeoutCheckThread implements Runnable {

    /**
     * 请求信息
     *
     * @since 0.0.7
     */
    private final ConcurrentHashMap<String, Long> requestMap;

    /**
     * 请求信息
     *
     * @since 0.0.7
     */
    private final ConcurrentHashMap<String, RpcResponse> responseMap;


    /**
     * 新建
     *
     * @param requestMap  请求 Map
     * @param responseMap 结果 map
     * @since 0.0.7
     */
    public InvokeTimeoutCheckThread(ConcurrentHashMap<String, Long> requestMap,
                                    ConcurrentHashMap<String, RpcResponse> responseMap) {
        ArgUtil.notNull(requestMap, "requestMap");

        this.requestMap = requestMap;
        this.responseMap = responseMap;
    }

    @Override
    public void run() {
        for (Map.Entry<String, Long> entry : requestMap.entrySet()) {
            long expireTime = entry.getValue();
            long currentTime = Times.systemTime();

            if (currentTime > expireTime) {
                final String key = entry.getKey();
                // 结果设置为超时，从请求 map 中移除, 同时设置 response 为超时
                responseMap.putIfAbsent(key, RpcResponseFactory.timeout());
                requestMap.remove(key);
            }
        }
    }

}
