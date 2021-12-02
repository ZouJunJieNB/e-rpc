package com.github.zou.rpc.server.service.impl;

import com.github.houbb.heaven.constant.PunctuationConst;
import com.github.houbb.heaven.util.common.ArgUtil;
import com.github.houbb.heaven.util.lang.reflect.ReflectMethodUtil;
import com.github.houbb.heaven.util.util.CollectionUtil;
import com.github.zou.rpc.common.exception.RpcRuntimeException;
import com.github.zou.rpc.server.config.service.ServiceConfig;
import com.github.zou.rpc.server.service.ServiceFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 默认服务仓库实现
 * @author zou
 * @since 0.0.6
 */
public class DefaultServiceFactory implements ServiceFactory {

    /**
     * 服务 map
     * @since 0.0.6
     */
    private Map<String, Object> serviceMap;

    /**
     * 直接获取对应的 method 信息
     * （1）key: serviceId:methodName:param1@param2@param3
     * （2）value: 对应的 method 信息
     */
    private Map<String, Method> methodMap;

    private static final DefaultServiceFactory INSTANCE = new DefaultServiceFactory();

    private DefaultServiceFactory(){}

    public static DefaultServiceFactory getInstance() {
        return INSTANCE;
    }

    /**
     * 服务注册一般在项目启动的时候，进行处理。
     * 属于比较重的操作，而且一个服务按理说只应该初始化一次。
     * 此处加锁为了保证线程安全。
     * @param serviceConfigList 服务配置列表
     * @return this
     */
    @Override
    public synchronized ServiceFactory registerServicesLocal(List<ServiceConfig> serviceConfigList) {
        ArgUtil.notEmpty(serviceConfigList, "serviceConfigList");

        // 集合初始化
        serviceMap = new HashMap<>(serviceConfigList.size());
        // 这里只是预估，一般为2个服务。
        methodMap = new HashMap<>(serviceConfigList.size()*2);

        for(ServiceConfig serviceConfig : serviceConfigList) {
            serviceMap.put(serviceConfig.id(), serviceConfig.reference());
        }

        // 存放方法名称
        for(Map.Entry<String, Object> entry : serviceMap.entrySet()) {
            String serviceId = entry.getKey();
            Object reference = entry.getValue();

            //获取所有方法列表
            Method[] methods = reference.getClass().getMethods();
            for(Method method : methods) {
                String methodName = method.getName();
                if(ReflectMethodUtil.isIgnoreMethod(methodName)) {
                    continue;
                }

                List<String> paramTypeNames = ReflectMethodUtil.getParamTypeNames(method);
                String key = buildMethodKey(serviceId, methodName, paramTypeNames);
                methodMap.put(key, method);
            }
        }

        return this;
    }


    @Override
    public Object invoke(String serviceId, String methodName, List<String> paramTypeNames, Object[] paramValues) {
        //参数校验
        ArgUtil.notEmpty(serviceId, "serviceId");
        ArgUtil.notEmpty(methodName, "methodName");

        // 提供 cache，可以根据前三个值快速定位对应的 method
        // 根据 method 进行反射处理。
        // 对于 paramTypes 进行 string 连接处理。
        final Object reference = serviceMap.get(serviceId);
        final String methodKey = buildMethodKey(serviceId, methodName, paramTypeNames);
        final Method method = methodMap.get(methodKey);

        try {
            return method.invoke(reference, paramValues);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RpcRuntimeException(e);
        }
    }

    /**
     * （1）多个之间才用 : 分隔
     * （2）参数之间采用 @ 分隔
     * @param serviceId 服务标识
     * @param methodName 方法名称
     * @param paramTypeNames 参数类型名称
     * @return 构建完整的 key
     * @since 0.0.6
     */
    private String buildMethodKey(String serviceId, String methodName, List<String> paramTypeNames) {
        String param = CollectionUtil.join(paramTypeNames, PunctuationConst.AT);
        return serviceId+PunctuationConst.COLON+methodName+PunctuationConst.COLON
                +param;
    }

}
