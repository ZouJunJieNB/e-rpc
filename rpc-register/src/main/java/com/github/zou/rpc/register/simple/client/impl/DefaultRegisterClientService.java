package com.github.zou.rpc.register.simple.client.impl;

import com.github.houbb.heaven.util.common.ArgUtil;
import com.github.houbb.heaven.util.guava.Guavas;
import com.github.houbb.heaven.util.lang.ObjectUtil;
import com.github.houbb.heaven.util.util.CollectionUtil;
import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;
import com.github.zou.rpc.common.constant.MessageTypeConst;
import com.github.zou.rpc.common.domain.entry.ServiceEntry;
import com.github.zou.rpc.common.domain.message.NotifyMessage;
import com.github.zou.rpc.common.domain.message.impl.NotifyMessages;
import com.github.zou.rpc.register.simple.client.RegisterClientService;
import io.netty.channel.Channel;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p> 默认客户端注册服务实现类 </p>
 *
 * @author zou
 * @since 1.0.0
 */
public class DefaultRegisterClientService implements RegisterClientService {

    private static final Log LOG = LogFactory.getLog(DefaultRegisterClientService.class);

    /**
     * 服务信息-客户端列表 map
     * key: serviceId
     * value: 对应的客户端列表信息。
     *
     * 客户端使用定期拉取的方式：
     * （1）传入 host 信息，返回对应的 service 列表。
     * （2）根据 service 列表，变化时定期推送给客户端。
     *
     * 只是在第一次采用拉取的方式，后面全部采用推送的方式。
     * （1）只有变更的时候，才会进行推送，保证实时性。
     * （2）客户端启动时拉取，作为保底措施。避免客户端不在线等情况。
     *
     * @since 1.0.0
     */
    private final Map<String, Set<Channel>> serviceClientChannelMap;

    public DefaultRegisterClientService() {
        this.serviceClientChannelMap = new ConcurrentHashMap<>();
    }

    @Override
    public void subscribe(ServiceEntry clientEntry, Channel clientChannel) {
        paramCheck(clientEntry);

        final String serviceId = clientEntry.serviceId();
        Set<Channel> channelSet = serviceClientChannelMap.get(serviceId);
        if (ObjectUtil.isNull(channelSet)) {
            channelSet = Guavas.newHashSet();
        }
        channelSet.add(clientChannel);
        serviceClientChannelMap.put(serviceId, channelSet);
    }

    @Override
    public void unSubscribe(ServiceEntry clientEntry, Channel clientChannel) {
        paramCheck(clientEntry);

        final String serviceId = clientEntry.serviceId();
        Set<Channel> channelSet = serviceClientChannelMap.get(serviceId);

        if (CollectionUtil.isEmpty(channelSet)) {
            // 服务列表为空
            LOG.info("[unSubscribe Client] remove host set is empty. entry: {}", clientEntry);
            return;
        }

        channelSet.remove(clientChannel);
        serviceClientChannelMap.put(serviceId, channelSet);
    }

    @Override
    public void registerNotify(String serviceId, ServiceEntry serviceEntry) {
        ArgUtil.notEmpty(serviceId, "serviceId");

        List<Channel> clientChannelList = clientChannelList(serviceId);
        if (CollectionUtil.isEmpty(clientChannelList)) {
            LOG.info("[Register] notify clients is empty for service: {}",
                    serviceId);
            return;
        }

        // 循环通知
        for(Channel channel : clientChannelList) {
            NotifyMessage notifyMessage = NotifyMessages.of(MessageTypeConst.SERVER_REGISTER_NOTIFY_CLIENT_REQ, serviceEntry);
            channel.writeAndFlush(notifyMessage);
        }
    }

    @Override
    public void unRegisterNotify(String serviceId, ServiceEntry serviceEntry) {
        ArgUtil.notEmpty(serviceId, "serviceId");

        List<Channel> clientChannelList = clientChannelList(serviceId);
        if (CollectionUtil.isEmpty(clientChannelList)) {
            LOG.info("[UnRegister] notify clients is empty for service: {}",
                    serviceId);
            return;
        }

        // 循环通知
        for(Channel channel : clientChannelList) {
            NotifyMessage notifyMessage = NotifyMessages.of(MessageTypeConst.SERVER_UNREGISTER_NOTIFY_CLIENT_REQ, serviceEntry);
            channel.writeAndFlush(notifyMessage);
        }
    }

    @Override
    public Collection<Channel> channels() {
        Set<Channel> resultSet = new HashSet<>();

        Collection<Set<Channel>> channelCollection =  serviceClientChannelMap.values();
        for(Set<Channel> set : channelCollection) {
            if(CollectionUtil.isNotEmpty(set)) {
                resultSet.addAll(set);
            }
        }
        return resultSet;
    }

    /**
     * 参数校验
     *
     * @param serviceEntry 入参信息
     * @since 1.0.0
     */
    private void paramCheck(final ServiceEntry serviceEntry) {
        ArgUtil.notNull(serviceEntry, "serverEntry");
        ArgUtil.notEmpty(serviceEntry.serviceId(), "serverEntry.serviceId");
//        ArgUtil.notEmpty(serviceEntry.ip(), "serverEntry.ip");
    }

    /**
     * 获取所有的客户端列表
     * @param serviceId 服务标识
     * @return 客户端列表标识
     * @since 1.0.0
     */
    private List<Channel> clientChannelList(String serviceId) {
        ArgUtil.notEmpty(serviceId, "serviceId");

        Set<Channel> clientSet = serviceClientChannelMap.get(serviceId);
        return Guavas.newArrayList(clientSet);
    }

}
