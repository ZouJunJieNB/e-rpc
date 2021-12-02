package com.github.zou.rpc.common.support.invoke.impl;

import com.github.houbb.heaven.util.lang.ObjectUtil;
import com.github.houbb.heaven.util.time.impl.Times;
import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;
import com.github.zou.rpc.common.rpc.domain.RpcResponse;
import com.github.zou.rpc.common.rpc.domain.impl.RpcResponseFactory;
import com.github.zou.rpc.common.support.invoke.InvokeManager;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 调用服务接口
 * @author zou
 * @since 0.0.6
 */
public class DefaultInvokeManager implements InvokeManager {

    private static final Log LOG = LogFactory.getLog(DefaultInvokeManager.class);

    /**
     * 请求序列号 map
     * （1）这里后期如果要添加超时检测，可以添加对应的超时时间。
     * 可以把这里调整为 map
     *
     * key: seqId 唯一标识一个请求
     * value: 存入该请求最长的有效时间。用于定时删除和超时判断。
     * @since 0.0.7
     */
    private final ConcurrentHashMap<String, Long> requestMap;

    /**
     * 响应结果
     * @since 0.0.6
     */
    private final ConcurrentHashMap<String, RpcResponse> responseMap;

    public DefaultInvokeManager() {
        requestMap = new ConcurrentHashMap<>();
        responseMap = new ConcurrentHashMap<>();

        final Runnable timeoutThread = new InvokeTimeoutCheckThread(requestMap, responseMap);
        Executors.newScheduledThreadPool(1)
                .scheduleAtFixedRate(timeoutThread,60, 60, TimeUnit.SECONDS);
    }

    @Override
    public InvokeManager addRequest(String seqId, long timeoutMills) {
        LOG.info("[Invoke] start add request for seqId: {}, timeoutMills: {}", seqId,
                timeoutMills);

        final long expireTime = Times.systemTime()+timeoutMills;
        requestMap.putIfAbsent(seqId, expireTime);

        return this;
    }

    @Override
    public InvokeManager addResponse(String seqId, RpcResponse rpcResponse) {
        // 1. 判断是否有效
        Long expireTime = this.requestMap.get(seqId);
        // 如果为空，可能是这个结果已经超时了，被定时 job 移除之后，响应结果才过来。直接忽略
        if(ObjectUtil.isNull(expireTime)) {
            LOG.warn("[Invoke] seqId: {} has been removed, maybe timeout!", seqId);
            return this;
        }

        //2. 判断是否超时
        if(Times.systemTime() > expireTime) {
            LOG.info("[Invoke] seqId:{} 信息已超时，直接返回超时结果。", seqId);
            rpcResponse = RpcResponseFactory.timeout();
        }

        // 这里放入之前，可以添加判断。
        // 如果 seqId 必须处理请求集合中，才允许放入。或者直接忽略丢弃。
        // 通知所有等待方
        responseMap.putIfAbsent(seqId, rpcResponse);
        LOG.info("[Invoke] 获取结果信息，seqId: {}, rpcResponse: {}", seqId, rpcResponse);
        LOG.info("[Invoke] seqId:{} 信息已经放入，通知所有等待方", seqId);

        synchronized (this) {
            this.notifyAll();
        }

        return this;
    }

    @Override
    public RpcResponse getResponse(String seqId) {
        try {
            RpcResponse rpcResponse = this.responseMap.get(seqId);
            if(ObjectUtil.isNotNull(rpcResponse)) {
                LOG.info("[Invoke] seq {} 对应结果已经获取: {}", seqId, rpcResponse);
            } else {
                // 进入等待
                while (rpcResponse == null) {
                    LOG.info("[Invoke] seq {} 对应结果为空，进入等待", seqId);
                    // 同步等待锁
                    synchronized (this) {
                        this.wait();
                    }

                    rpcResponse = this.responseMap.get(seqId);
                    LOG.info("[Invoke] seq {} 对应结果已经获取: {}", seqId, rpcResponse);
                }
            }

            // 移除 request
            this.requestMap.remove(seqId);
            return rpcResponse;
        } catch (InterruptedException e) {
            LOG.error("[Invoke] get response meet InterruptedException ex", e);
            return RpcResponseFactory.interrupted();
        }
    }

    @Override
    public boolean remainsRequest() {
        return this.requestMap.size() > 0;
    }

    @Override
    public DefaultInvokeManager removeReqAndResp(String seqId) {
        LOG.info("[Invoke] remove the request and response for seqId: {}", seqId);
        // 移除这个 key
        this.requestMap.remove(seqId);
        this.responseMap.remove(seqId);
        return this;
    }

}
