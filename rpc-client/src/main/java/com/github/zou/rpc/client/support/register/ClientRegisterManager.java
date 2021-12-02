package com.github.zou.rpc.client.support.register;


import com.github.zou.rpc.client.model.ClientQueryServerChannelConfig;
import com.github.zou.rpc.common.domain.entry.ServiceEntry;
import com.github.zou.rpc.common.domain.message.body.RegisterCenterAddNotifyBody;
import com.github.zou.rpc.common.domain.message.body.RegisterCenterRemoveNotifyBody;
import com.github.zou.rpc.common.rpc.domain.RpcChannelFuture;
import io.netty.channel.Channel;

import java.util.List;

/**
 * <p> 客户端注册中心服务接口 </p>
 *
 * @author zou
 * @since 1.0.0
 */
public interface ClientRegisterManager {

    /**
     * 初始化查询服务端列表
     *
     * 使用场景：第一次查询的时候使用
     *
     * @param config 查询配置信息
     */
    void initServerChannelFutureList(ClientQueryServerChannelConfig config);

    /**
     * 查询服务端对应的列表
     * @param serviceId 服务标识
     * @return 结果
     */
    List<RpcChannelFuture> queryServerChannelFutures(final String serviceId);

    /**
     * 取消订阅所有服务端信息
     */
    void unSubscribeServerAll();

    /**
     * 订阅指定服务端信息
     * @param serviceId 服务标识
     */
    void subscribeServer(final String serviceId);

    /**
     * 服务端注册通知
     * @param serviceEntry 服务端信息
     */
    void serverRegisterNotify(ServiceEntry serviceEntry);

    /**
     * 服务端取消注册通知
     * @param serviceEntry 服务端信息
     */
    void serverUnRegisterNotify(ServiceEntry serviceEntry);

    /**
     * 添加注册中心的 channel
     * @param body 对象
     * @param channel channel
     */
    void addRegisterChannel(RegisterCenterAddNotifyBody body, Channel channel);

    /**
     * 移除注册中心的 channel
     * @param body 地址
     */
    void removeRegisterChannel(RegisterCenterRemoveNotifyBody body);

}
