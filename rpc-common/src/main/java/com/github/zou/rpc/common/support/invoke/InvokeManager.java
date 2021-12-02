package com.github.zou.rpc.common.support.invoke;

import com.github.zou.rpc.common.rpc.domain.RpcResponse;

/**
 * 调用服务接口
 * @author zou
 * @since 1.0.0
 */
public interface InvokeManager {

    /**
     * 添加请求信息
     * @param seqId 序列号
     * @param timeoutMills 超时时间
     * @return this
     */
    InvokeManager addRequest(final String seqId,
                             final long timeoutMills);

    /**
     * 放入结果
     * @param seqId 唯一标识
     * @param rpcResponse 响应结果
     * @return this
     */
    InvokeManager addResponse(final String seqId, final RpcResponse rpcResponse);

    /**
     * 获取标志信息对应的结果
     * （1）需要移除对应的结果信息
     * （2）需要移除对应的请求信息
     *
     * 将移除统一放在这里，实际上也有点不太合理。
     * 但是此处考虑到写入的时间较短，可以忽略不计。
     * 如果非要考虑非常细致，可以将移除的地方，单独暴露一个方法，但是个人觉得没有必要。
     * @param seqId 序列号
     * @return 结果
     */
    RpcResponse getResponse(final String seqId);

    /**
     * 是否依然包含请求待处理
     * @return 是否
     */
    boolean remainsRequest();

    /**
     * 移除请求和响应
     * @param seqId 唯一标识
     * @return this
     */
    InvokeManager removeReqAndResp(final String seqId);
}
