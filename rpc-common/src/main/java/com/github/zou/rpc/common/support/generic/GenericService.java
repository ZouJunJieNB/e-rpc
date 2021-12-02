package com.github.zou.rpc.common.support.generic;


import com.github.zou.rpc.common.exception.GenericException;

/**
 * 泛化调用接口
 * （1）接口直接使用 dubbo 的接口
 *
 *
 * 【应用场景】
 * 泛接口实现方式主要用于服务器端没有API接口及模型类元的情况，参数及返回值中的所有POJO均用Map表示，通常用于框架集成，比如：实现一个通用的远程服务Mock框架，可通过实现GenericService接口处理所有服务请求。
 *
 * 【服务端】
 * 服务端代码不需要做任何调整。
 * 客户端泛化调用进行相关调整即可。
 *
 * 【客户端】
 *
 * @author zou
 * @since 1.0.0
 */
public interface GenericService {

    /**
     * Generic invocation
     *
     * @param method         Method name, e.g. findPerson. If there are overridden methods, parameter info is
     *                       required, e.g. findPerson(java.lang.String)
     * @param parameterTypes Parameter types
     * @param args           Arguments
     * @return invocation return value
     * @throws GenericException potential exception thrown from the invocation
     */
    Object $invoke(String method, String[] parameterTypes, Object[] args) throws GenericException;


}
