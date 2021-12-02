package com.github.zou.rpc.client.proxy.impl;


import com.github.houbb.heaven.util.id.impl.Ids;
import com.github.houbb.heaven.util.lang.reflect.ReflectMethodUtil;
import com.github.houbb.heaven.util.time.impl.Times;
import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;
import com.github.zou.rpc.client.proxy.ReferenceProxy;
import com.github.zou.rpc.client.proxy.RemoteInvokeService;
import com.github.zou.rpc.client.proxy.ServiceContext;
import com.github.zou.rpc.common.rpc.domain.impl.DefaultRpcRequest;
import com.github.zou.rpc.common.support.inteceptor.RpcInterceptor;
import com.github.zou.rpc.common.support.inteceptor.impl.DefaultRpcInterceptorContext;
import com.github.zou.rpc.common.support.status.enums.StatusEnum;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 参考：https://blog.csdn.net/u012240455/article/details/79210250
 *
 * （1）方法执行并不需要一定要有实现类。
 * （2）直接根据反射即可处理相关信息。
 * （3）rpc 是一种强制根据接口进行编程的实现方式。
 * @author zou
 * @since 1.0.0
 */
public class DefaultReferenceProxy<T> implements ReferenceProxy<T> {


    /**
     * 代理上下文
     * （1）这个信息不应该被修改，应该和指定的 service 紧密关联。
     * @since 0.0.6
     */
    private final ServiceContext<T> proxyContext;

    /**
     * 远程调用接口
     * @since 0.1.1
     */
    private final RemoteInvokeService remoteInvokeService;

    public DefaultReferenceProxy(ServiceContext<T> proxyContext, RemoteInvokeService remoteInvokeService) {
        this.proxyContext = proxyContext;
        this.remoteInvokeService = remoteInvokeService;
    }

    /**
     * 反射调用
     * @param proxy 代理
     * @param method 方法
     * @param args 参数
     * @return 结果
     * @throws Throwable 异常
     * @since 0.0.6
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 状态判断
        final String traceId = Ids.uuid32();
        final int statusCode = proxyContext.statusManager().status();
        StatusEnum.assertEnable(statusCode);
        final long createTime = Times.systemTime();

        //1. 拦截器
        final RpcInterceptor rpcInterceptor = proxyContext.interceptor();
        final DefaultRpcInterceptorContext interceptorContext = DefaultRpcInterceptorContext.newInstance()
                .traceId(traceId)
                .params(args)
                .startTime(createTime);
        rpcInterceptor.before(interceptorContext);

        // 构建基本调用参数
        DefaultRpcRequest rpcRequest = new DefaultRpcRequest();
        rpcRequest.serviceId(proxyContext.serviceId());
        rpcRequest.createTime(createTime);
        rpcRequest.paramValues(args);
        rpcRequest.paramTypeNames(ReflectMethodUtil.getParamTypeNames(method));
        rpcRequest.methodName(method.getName());
        rpcRequest.returnType(method.getReturnType());
        rpcRequest.timeout(proxyContext.timeout());
        rpcRequest.callType(proxyContext.callType());

        //proxyContext 中应该是属于当前 service 的对应信息。
        // 每一次调用，对应的 invoke 信息应该是不通的，需要创建新的对象去传递信息
        // rpcRequest 因为要涉及到网络间传输，尽可能保证其简洁性。
        DefaultRemoteInvokeContext<T> context = new DefaultRemoteInvokeContext<>();
        context.request(rpcRequest);
        context.traceId(traceId);
        context.retryTimes(2);
        context.serviceProxyContext(proxyContext);
        context.remoteInvokeService(remoteInvokeService);

        //3. 执行远程调用
        Object result = remoteInvokeService.remoteInvoke(context);

        //4. 拦截器结束
        final long endTime = Times.systemTime();
        interceptorContext.endTime(endTime)
                .result(result);
        rpcInterceptor.after(interceptorContext);
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T proxy() {
        final Class<T> interfaceClass = proxyContext.serviceInterface();
        ClassLoader classLoader = interfaceClass.getClassLoader();
        Class<?>[] interfaces = new Class[]{interfaceClass};
        return (T) Proxy.newProxyInstance(classLoader, interfaces, this);
    }

}
