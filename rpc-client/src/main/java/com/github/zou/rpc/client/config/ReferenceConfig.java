package com.github.zou.rpc.client.config;

import com.github.zou.rpc.client.support.filter.RpcFilter;
import com.github.zou.rpc.common.constant.enums.CallTypeEnum;
import com.github.zou.rpc.client.support.fail.enums.FailTypeEnum;
import com.github.zou.rpc.common.support.balance.ILoadBalance;
import com.github.zou.rpc.common.support.inteceptor.RpcInterceptor;

/**
 * 引用配置类
 *
 * 后期配置：
 * （1）timeout 调用超时时间
 * （2）version 服务版本处理
 * （3）callType 调用方式 oneWay/sync/async
 * （4）check 是否必须要求服务启动。
 *
 * spi:
 * （1）codec 序列化方式
 * （2）netty 网络通讯架构
 * （3）load-balance 负载均衡
 * （4）失败策略 fail-over/fail-fast
 *
 * filter:
 * （1）路由
 * （2）耗时统计 monitor 服务治理
 *
 * 优化思考：
 * （1）对于唯一的 serviceId，其实其 interface 是固定的，是否可以省去？
 * @author zou
 * @since 1.0.0
 */
public interface ReferenceConfig<T> {



    /**
     * 获取对应的引用实现
     * @return 引用代理类
     */
    T reference();


    /**
     * 设置服务标识
     * @param serviceId 服务标识
     * @return this
     */
    ReferenceConfig<T> serviceId(final String serviceId);


    /**
     * 设置服务接口信息
     * @param serviceInterface 服务接口信息
     * @return this
     */
    ReferenceConfig<T> serviceInterface(final Class<T> serviceInterface);

    /**
     * 设置服务地址信息
     * （1）单个写法：ip:port:weight
     * （2）集群写法：ip1:port1:weight1,ip2:port2:weight2
     *
     * 其中 weight 权重可以不写，默认为1.
     *
     * @param addresses 地址列表信息
     * @return this
     */
    ReferenceConfig<T> addresses(final String addresses);

    /**
     * 设置超时时间
     * @param timeoutInMills 超时时间
     * @return this
     */
    ReferenceConfig<T> timeout(final long timeoutInMills);


    /**
     * 是否订阅发现服务
     * 注意：如果指定 {@link #addresses(String)} 信息，这个属性将会失效。
     * @param subscribe 是否订阅模式
     * @return this
     */
    ReferenceConfig<T> subscribe(final boolean subscribe);


    /**
     * 注册中心地址
     * （1）正常使用中，这个属性是可以固定写死，对使用者不可见的。
     * @param addresses 注册中心地址
     * @return this
     */
    ReferenceConfig<T> registerCenter(final String addresses);

    /**
     * 调用方式
     * @param callTypeEnum 调用方式
     * @return this
     */
    ReferenceConfig<T> callType(final CallTypeEnum callTypeEnum);

    /**
     * 失败类型
     * @param failTypeEnum 失败策略枚举
     * @since 0.1.1
     * @return this
     */
    ReferenceConfig<T> failType(final FailTypeEnum failTypeEnum);

    /**
     * 设置是否进行泛化处理
     * @param generic 是否进行泛化处理
     * @since 0.1.2
     * @return this
     */
    ReferenceConfig<T> generic(final boolean generic);

    /**
     * 调用拦截器
     * @param rpcInterceptor 拦截器信息
     * @return this
     * @since 0.1.4
     */
    ReferenceConfig<T> rpcInterceptor(final RpcInterceptor rpcInterceptor);

    /**
     * 是否检测状态
     * @param check 是否检测
     * @return this
     * @since 0.1.5
     */
    ReferenceConfig<T> check(final boolean check);

    /**
     * 设置过滤器
     * @param rpcFilter 过滤器
     * @return this
     * @since 0.2.0
     */
    ReferenceConfig<T> rpcFilter(final RpcFilter rpcFilter);

    /**
     * 负载均衡
     * @param loadBalance 负载均衡
     * @return this
     * @since 0.2.0
     */
    ReferenceConfig<T> loadBalance(final ILoadBalance loadBalance);

}
