package com.github.zou.rpc.register.support.register;

import com.github.zou.rpc.common.domain.entry.ServiceEntry;
import com.github.zou.rpc.register.simple.client.RegisterClientService;
import com.github.zou.rpc.register.simple.server.RegisterServerService;
import io.netty.channel.Channel;


/**
 *
 * FailBackRegistry 继承 AbstractRegistry，实现了 register，subscribe等通用法，并增加 doRegister，doSubscribe 等模板方法，交由子类实现。
 * todo 重试功能暂未实现
 * @author zou
 * @since 1.0.0
 */
public abstract class FailBackRegistry extends AbstractRpcRegistry {

    public FailBackRegistry(RegisterServerService registerServerService, RegisterClientService registerClientService) {
        super(registerServerService, registerClientService);
    }

    @Override
    public void register(ServiceEntry serviceEntry, Channel channel) {
        // 先注册本地
        super.register(serviceEntry,channel);
        // 注册到注册中心 todo 失败重试待做
        doRegister( serviceEntry,  channel);
    }

    @Override
    public void unRegister(ServiceEntry serviceEntry) {
        super.unRegister(serviceEntry);
        doUnRegister(serviceEntry);
    }

    @Override
    public void subscribe(ServiceEntry clientEntry, final Channel channel) {
        super.subscribe(clientEntry,channel);
        doSubscribe(clientEntry,channel);
    }

    @Override
    public void unSubscribe(ServiceEntry clientEntry, Channel channel) {
        super.unSubscribe(clientEntry,channel);
        doUnSubscribe(clientEntry,channel);
    }

    // ==== 模板方法 ====

    public abstract void doRegister(final ServiceEntry serviceEntry, Channel channel);

    public abstract void doUnRegister(final ServiceEntry serviceEntry);

    public abstract void doSubscribe(final ServiceEntry serviceEntry, final Channel channel);

    public abstract void doUnSubscribe(final ServiceEntry server, final Channel channel);

}
