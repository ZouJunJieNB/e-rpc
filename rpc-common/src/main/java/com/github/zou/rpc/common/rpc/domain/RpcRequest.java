package com.github.zou.rpc.common.rpc.domain;

import com.github.zou.rpc.common.constant.enums.CallTypeEnum;

import java.util.List;

/**
 *
 * 序列化相关处理
 * （1）调用创建时间-createTime
 * （2）调用方式 callType
 * （3）超时时间 timeOut
 *
 * 额外信息：
 * （1）上下文信息
 *
 * @author zou
 * @since 1.0.0
 */
public interface RpcRequest extends BaseRpc {

    /**
     * 创建时间
     * @return 创建时间
     */
    long createTime();

    /**
     * 服务唯一标识
     * @return 服务唯一标识
     */
    String serviceId();

    /**
     * 方法名称
     * @return 方法名称
     */
    String methodName();

    /**
     * 方法类型名称列表
     * @return 名称列表
     */
    List<String> paramTypeNames();

    /**
     * 调用参数值
     * @return 参数值数组
     */
    Object[] paramValues();

    /**
     * 返回值类型
     * @return 返回值类型
     */
    Class returnType();

    /**
     * 超时时间
     * @return 超时时间
     */
    long timeout();

    /**
     * 调用方式
     * @return 调用方式
     */
    CallTypeEnum callType();
}
