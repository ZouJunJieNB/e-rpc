package com.github.zou.rpc.register.support.hook;

import com.github.houbb.heaven.util.net.NetUtil;
import com.github.zou.rpc.common.constant.MessageTypeConst;
import com.github.zou.rpc.common.domain.message.NotifyMessage;
import com.github.zou.rpc.common.domain.message.body.RegisterCenterAddNotifyBody;
import com.github.zou.rpc.common.domain.message.body.RegisterCenterRemoveNotifyBody;
import com.github.zou.rpc.common.domain.message.impl.NotifyMessages;
import com.github.zou.rpc.common.support.hook.AbstractShutdownHook;
import com.github.zou.rpc.register.simple.client.RegisterClientService;
import com.github.zou.rpc.register.simple.server.RegisterServerService;
import io.netty.channel.Channel;

import java.util.Collection;

/**
 * 注册中心 shutdown
 * @since 0.1.8
 */
public class RegisterCenterShutdownHook extends AbstractShutdownHook {

    /**
     * 服务端
     */
    private final RegisterServerService registerServerService;
    /**
     * 客户端
     */
    private final RegisterClientService registerClientService;

    /**
     * 端口号
     */
    private final int port;

    public RegisterCenterShutdownHook(RegisterServerService registerServerService,
                                      RegisterClientService registerClientService,
                                      int port) {
        this.registerServerService = registerServerService;
        this.registerClientService = registerClientService;
        this.port = port;
    }

    @Override
    protected void doHook() {
        String ip = NetUtil.getLocalIp();
        RegisterCenterRemoveNotifyBody addNotifyBody = new RegisterCenterRemoveNotifyBody();
        addNotifyBody.ip(ip);
        addNotifyBody.port(port);
        NotifyMessage notifyMessage = NotifyMessages.of(MessageTypeConst.REGISTER_CENTER_REMOVE_NOTIFY, addNotifyBody);

        //1. 通知所有的服务端
        //TODO: 这些 channel 应该进行一次封装，保留原始的 ip:port 信息
        Collection<Channel> serverList = registerServerService.channels();
        for(Channel channel : serverList) {
            channel.writeAndFlush(notifyMessage);
        }

        //2. 通知所有的客户端
        Collection<Channel> clientList = registerClientService.channels();
        for(Channel channel : clientList) {
            channel.writeAndFlush(notifyMessage);
            System.out.println(channel.isActive());
        }
        try {
            // 休眠两秒，因为channel发送是异步的，可能已经结束了但是还没及时推送过去
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
