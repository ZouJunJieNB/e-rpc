/*
 * Copyright (c)  2019. houbinbin Inc.
 * rpc All rights reserved.
 */

package com.github.zou.rpc.common.rpc.domain.impl;


import com.github.houbb.heaven.util.lang.ObjectUtil;
import com.github.houbb.heaven.util.lang.reflect.PrimitiveUtil;
import com.github.zou.rpc.common.exception.RpcRuntimeException;
import com.github.zou.rpc.common.rpc.domain.RpcResponse;

/**
 * 对 rpc 的response响应结果获取和设置进行封装的类
 * @author zou
 * @since 1.0.0
 */
public final class RpcResponses {

    private RpcResponses(){}

    /**
     * 设置结果
     * @param object 结果
     * @param tClass 结果类型
     * @return 结果
     */
    public static RpcResponse result(final Object object,
                                     final Class tClass) {
        DefaultRpcResponse rpcResponse = new DefaultRpcResponse();
        if(ObjectUtil.isNotNull(object)) {
            rpcResponse.result(object);
        } else {
            // 处理基本类型的默认值，避免 NPE
            Object defaultVal = PrimitiveUtil.getDefaultValue(tClass);
            rpcResponse.result(defaultVal);
        }
        return rpcResponse;
    }

    /**
     * 从RpcResponse获取结果
     * @param rpcResponse 响应
     * @param returnType 返回值类型
     * @return 结果
     * 如果有异常，则直接抛出异常信息。
     */
    public static Object getResult(final RpcResponse rpcResponse,
                                  final Class returnType) {
        if(ObjectUtil.isNull(rpcResponse)) {
            // 根据返回类型处理
            return PrimitiveUtil.getDefaultValue(returnType);
        }

        // 处理异常信息
        Throwable throwable = rpcResponse.error();
        if(ObjectUtil.isNotNull(throwable)) {
            throw new RpcRuntimeException(throwable);
        }

        // 处理结果信息
        Object result = rpcResponse.result();
        if(ObjectUtil.isNotNull(result)) {
            return result;
        }
        return PrimitiveUtil.getDefaultValue(returnType);
    }

    /**
     * 获取结果
     * @param rpcResponse 响应
     * @return 结果
     * 如果有异常，则直接抛出异常信息。
     */
    public static Object getResult(final RpcResponse rpcResponse) {
        return getResult(rpcResponse, Object.class);
    }

}
