
package com.github.zou.rpc.client.handler;


import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;
import com.github.zou.rpc.common.support.invoke.InvokeManager;
import com.github.zou.rpc.client.support.register.ClientRegisterManager;
import com.github.zou.rpc.common.constant.MessageTypeConst;
import com.github.zou.rpc.common.domain.entry.ServiceEntry;
import com.github.zou.rpc.common.domain.message.NotifyMessage;
import com.github.zou.rpc.common.domain.message.body.RegisterCenterAddNotifyBody;
import com.github.zou.rpc.common.domain.message.body.RegisterCenterRemoveNotifyBody;
import com.github.zou.rpc.common.domain.message.impl.NotifyMessages;
import com.github.zou.rpc.common.rpc.domain.RpcResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * <p> 客户端注册中心处理类 </p>
 *
 * @author zou
 * @since 1.0.0
 */
public class RpcClientRegisterHandler extends SimpleChannelInboundHandler {

    private static final Log log = LogFactory.getLog(RpcClientRegisterHandler.class);

    /**
     * 注册服务
     * @since 1.0.0
     */
    private final InvokeManager invokeManager;

    /**
     * 客户端注册中心管理类
     * @since 0.1.8
     */
    private final ClientRegisterManager clientRegisterManager;

    public RpcClientRegisterHandler(InvokeManager invokeManager,
                                    ClientRegisterManager clientRegisterManager) {
        this.invokeManager = invokeManager;
        this.clientRegisterManager = clientRegisterManager;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        NotifyMessage notifyMessage = (NotifyMessage) msg;
        Object body = notifyMessage.body();
        String type = NotifyMessages.type(notifyMessage);
        String seqId = notifyMessage.seqId();
        log.info("[Register Client] received message type: {}, seqId: {} ", type,
                seqId);

        // 回写
        final Channel channel = ctx.channel();
        switch (type) {
            case MessageTypeConst.CLIENT_LOOK_UP_SERVER_RESP:
                RpcResponse rpcResponse = (RpcResponse) body;
                log.info("[Register Client] Register response is :{}", rpcResponse);
                invokeManager.addResponse(rpcResponse.seqId(), rpcResponse);
                break;

            case MessageTypeConst.SERVER_REGISTER_NOTIFY_CLIENT_REQ:
                ServiceEntry serviceEntry = (ServiceEntry) body;
                clientRegisterManager.serverRegisterNotify(serviceEntry);
                break;

            case MessageTypeConst.SERVER_UNREGISTER_NOTIFY_CLIENT_REQ:
                ServiceEntry serviceEntry2 = (ServiceEntry) body;
                clientRegisterManager.serverUnRegisterNotify(serviceEntry2);
                break;

            case MessageTypeConst.REGISTER_CENTER_ADD_NOTIFY:
                RegisterCenterAddNotifyBody addNotifyBody = (RegisterCenterAddNotifyBody) body;
                clientRegisterManager.addRegisterChannel(addNotifyBody, channel);
                break;

            case MessageTypeConst.REGISTER_CENTER_REMOVE_NOTIFY:
                RegisterCenterRemoveNotifyBody removeNotifyBody = (RegisterCenterRemoveNotifyBody) body;
                clientRegisterManager.removeRegisterChannel(removeNotifyBody);
                break;

            default:
                log.warn("[Register Client] not support type: {} and seqId: {}",
                        type, seqId);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        // 每次用完要关闭，不然拿不到response
//        ctx.flush();
//        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("[Rpc Client] meet ex ", cause);
        ctx.close();
    }

}
