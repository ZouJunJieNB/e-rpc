package com.github.zou.rpc.client.proxy.impl;


import com.github.houbb.heaven.util.guava.Guavas;
import com.github.houbb.heaven.util.id.impl.Ids;
import com.github.houbb.heaven.util.time.impl.Times;
import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;
import com.github.zou.rpc.client.proxy.RemoteInvokeService;
import com.github.zou.rpc.client.proxy.ServiceContext;
import com.github.zou.rpc.common.exception.GenericException;
import com.github.zou.rpc.common.rpc.domain.impl.DefaultRpcRequest;
import com.github.zou.rpc.common.support.generic.GenericService;
import com.github.zou.rpc.common.support.inteceptor.RpcInterceptor;
import com.github.zou.rpc.common.support.inteceptor.RpcInterceptorContext;
import com.github.zou.rpc.common.support.inteceptor.impl.DefaultRpcInterceptorContext;
import com.github.zou.rpc.common.support.status.enums.StatusEnum;

import java.util.List;

/**
 * 泛化调用
 * TODO: 想办法对两个方法进行整合。
 * @author zou
 * @since 1.0.0
 */
public class GenericReferenceProxy implements GenericService {

    private static final Log LOG = LogFactory.getLog(GenericReferenceProxy.class);

    /**
     * 代理上下文
     * （1）这个信息不应该被修改，应该和指定的 service 紧密关联。
     * @since 0.1.3
     */
    private final ServiceContext proxyContext;

    /**
     * 远程调用接口
     * @since 0.1.3
     */
    private final RemoteInvokeService remoteInvokeService;

    public GenericReferenceProxy(ServiceContext proxyContext, RemoteInvokeService remoteInvokeService) {
        this.proxyContext = proxyContext;
        this.remoteInvokeService = remoteInvokeService;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object $invoke(String method, String[] parameterTypes, Object[] args) throws GenericException {
        // 状态判断
        final String traceId = Ids.uuid32();
        final int statusCode = proxyContext.statusManager().status();
        StatusEnum.assertEnable(statusCode);


        //1. 拦截器
        final RpcInterceptor rpcInterceptor = proxyContext.interceptor();
        final RpcInterceptorContext rpcInterceptorContext = DefaultRpcInterceptorContext.newInstance()
                .traceId(traceId);
        rpcInterceptor.before(rpcInterceptorContext);

        // 构建基本调用参数
        final long createTime = Times.systemTime();
        Object[] actualArgs = new Object[]{method, parameterTypes, args};
        DefaultRpcRequest rpcRequest = new DefaultRpcRequest();
        rpcRequest.serviceId(proxyContext.serviceId());
        rpcRequest.createTime(createTime);
        rpcRequest.paramValues(actualArgs);
        List<String> paramTypeNames = Guavas.newArrayList();
        paramTypeNames.add("java.lang.String");
        paramTypeNames.add("[Ljava.lang.String;");
        paramTypeNames.add("[Ljava.lang.Object;");
        rpcRequest.paramTypeNames(paramTypeNames);
        rpcRequest.methodName("$invoke");
        rpcRequest.returnType(Object.class);
        rpcRequest.timeout(proxyContext.timeout());
        rpcRequest.callType(proxyContext.callType());

        //proxyContext 中应该是属于当前 service 的对应信息。
        // 每一次调用，对应的 invoke 信息应该是不通的，需要创建新的对象去传递信息
        // rpcRequest 因为要涉及到网络间传输，尽可能保证其简洁性。
        DefaultRemoteInvokeContext context = new DefaultRemoteInvokeContext();
        context.request(rpcRequest);
        context.traceId(traceId);
        context.retryTimes(2);
        context.serviceProxyContext(proxyContext);
        context.remoteInvokeService(remoteInvokeService);

        //3. 执行远程调用
        Object result = remoteInvokeService.remoteInvoke(context);
        rpcInterceptor.after(rpcInterceptorContext);
        return result;
    }

}
