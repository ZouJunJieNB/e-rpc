package com.github.zou.rpc.register.support.register;


import com.github.houbb.heaven.util.util.CollectionUtil;
import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;
import com.github.zou.rpc.common.constant.MessageTypeConst;
import com.github.zou.rpc.common.domain.entry.ServiceEntry;
import com.github.zou.rpc.common.domain.entry.impl.ServiceEntryBuilder;
import com.github.zou.rpc.common.domain.message.NotifyMessage;
import com.github.zou.rpc.common.domain.message.body.ServerHeartbeatBody;
import com.github.zou.rpc.common.domain.message.impl.NotifyMessages;
import com.github.zou.rpc.common.rpc.domain.RpcResponse;
import com.github.zou.rpc.common.rpc.domain.impl.DefaultRpcResponse;
import com.github.zou.rpc.common.util.IpUtils;
import com.github.zou.rpc.register.simple.client.RegisterClientService;
import com.github.zou.rpc.register.simple.server.RegisterServerService;
import com.github.zou.rpc.register.spi.RpcRegistry;
import com.google.common.collect.Lists;
import io.netty.channel.Channel;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.github.zou.rpc.common.constant.PunctuationConst.COLON;
import static com.github.zou.rpc.common.constant.PunctuationConst.COMMA;

/**
 *
 * 注册中心通用缓存层
 *
 * 如果每次服务调用都需要调用注册中心实时查询可用服务列表，不但会让注册中心承受巨大的流量压力，还会产生额外的网络请求，导致系统性能下降。
 * 其次注册中心需要非强依赖，其宕机不能影响正常的服务调用。
 *
 * 这里只实现了内存缓存，磁盘缓存暂未实现
 *
 * @author zou
 * @since 1.0.0
 */
public abstract class AbstractRpcRegistry implements RpcRegistry {

    private static final Log log = LogFactory.getLog(AbstractRpcRegistry.class);

    /**
     * 服务端信息管理
     * @since 1.0.0
     */
    private final RegisterServerService registerServerService;

    /**
     * 客户端信息管理
     * @since 1.0.0
     */
    private final RegisterClientService registerClientService;

    /**
     * 服务端心跳 map
     * @since 0.2.0
     */
    private final Map<String, Long> serverHeartbeatMap;

    public AbstractRpcRegistry(RegisterServerService registerServerService, RegisterClientService registerClientService) {
        this.registerServerService = registerServerService;
        this.registerClientService = registerClientService;
        this.serverHeartbeatMap = new ConcurrentHashMap<>();

        // 服务端心跳定时处理。转为局部变量
        ScheduledExecutorService serverHeartBeatExecutor = Executors.newSingleThreadScheduledExecutor();
        final Runnable runnable = new ServerHeartBeatThread();
        serverHeartBeatExecutor.scheduleAtFixedRate(runnable, 60, 60, TimeUnit.SECONDS);
    }

    /**
     * 心跳线程
     * @since 0.2.0
     */
    private class ServerHeartBeatThread implements Runnable {
        /**
         * 限制毫秒数
         * @since 0.2.0
         */
        private final long limitMills = 120 * 1000;

        @Override
        public void run() {
            // 遍历集合，找到过期的服务端
            List<String> expiredServices = getExpiredServiceList(limitMills);
            if(CollectionUtil.isEmpty(expiredServices)) {
                log.debug("[HEARTBEAT] 本次过期服务检测不存在，忽略。");
                return;
            }

            // 遍历过期的服务端，执行
            for(String ipPort : expiredServices) {
                Collection<ServiceEntry> serviceEntries = registerServerService.serviceEntries(ipPort);
                if(CollectionUtil.isEmpty(serviceEntries)) {
                    log.debug("[HEARTBEAT] ipPort {} 本次过期服务列表不存在，忽略。", ipPort);
                    continue;
                }

                for(ServiceEntry serviceEntry : serviceEntries) {
                    log.debug("[HEARTBEAT] serviceEntry {} 已过期，开始取消注册处理。", serviceEntry);
                    unRegister(serviceEntry);
                    log.debug("[HEARTBEAT] serviceEntry {} 已过期，完成取消注册处理。", serviceEntry);
                }
            }
        }
    }



    /**
     * 获取过期的服务列表
     * @param limitMills 超时时间
     * @return 结果
     * @since 0.2.0
     */
    private List<String> getExpiredServiceList(final long limitMills) {
        // 遍历集合，找到过期的服务端
        List<String> expiredServices = new ArrayList<>();
        long currentMills = System.currentTimeMillis();
        for(Map.Entry<String,Long> entry : serverHeartbeatMap.entrySet()) {
            String key = entry.getKey();
            long time = entry.getValue();

            long differMills = currentMills - time;
            if(differMills > limitMills) {
                log.debug("[HEARTBEAT] ip:port {} has been expired for {}", differMills);
                expiredServices.add(key);
            }
        }
        return expiredServices;
    }

    @Override
    public void register(ServiceEntry serviceEntry, Channel channel) {
        List<ServiceEntry> serviceEntryList = registerServerService.register(serviceEntry, channel);
        // 通知监听者
        registerClientService.registerNotify(serviceEntry.serviceId(), serviceEntry);
    }

    @Override
    public void unRegister(ServiceEntry serviceEntry) {
        List<ServiceEntry> serviceEntryList = registerServerService.unRegister(serviceEntry);

        // 通知监听者
        registerClientService.unRegisterNotify(serviceEntry.serviceId(), serviceEntry);
    }

    @Override
    public void subscribe(ServiceEntry clientEntry, final Channel channel) {
        registerClientService.subscribe(clientEntry, channel);
    }

    @Override
    public void unSubscribe(ServiceEntry clientEntry, Channel channel) {
        registerClientService.unSubscribe(clientEntry, channel);
    }

    @Override
    public void lookUp(String seqId, ServiceEntry clientEntry, Channel channel) {
        final String serviceId = clientEntry.serviceId();
        List<ServiceEntry> serviceEntryList = registerServerService.lookUp(serviceId);
        List<ServiceEntry> zookeeperServiceList = doLookUp(serviceId);
        List<ServiceEntry> newArray = mergeServiceEntryList(serviceEntryList,zookeeperServiceList);

        // 回写
        // 为了复用原先的相应结果，此处直接使用 rpc response
        RpcResponse rpcResponse = DefaultRpcResponse.newInstance().seqId(seqId)
                .result(newArray);
        NotifyMessage notifyMessage = NotifyMessages.of(MessageTypeConst.CLIENT_LOOK_UP_SERVER_RESP, seqId, rpcResponse);
        channel.writeAndFlush(notifyMessage);
    }

    /**
     * 调用具体注册中心的服务
     * @param serviceId
     * @return
     */
    public abstract List<ServiceEntry> doLookUp(String serviceId);

    @Override
    public void serverHeartbeat(ServerHeartbeatBody heartbeatBody, Channel channel) {
        String ip = heartbeatBody.ip();
        int port = heartbeatBody.port();

        // 存储当前的时间
        String key = IpUtils.ipPort(ip, port);
        long time = heartbeatBody.time();
        serverHeartbeatMap.put(key, time);
        log.debug("[HEARTBEAT] 接收到服务端的心跳 {}", heartbeatBody);
    }

    protected List<ServiceEntry> convertServiceEntry(List<String> stringList,String serverId){
        List<ServiceEntry> resultList = Lists.newArrayList();

        if(CollectionUtil.isEmpty(stringList)){
            return resultList;
        }

        for (String s : stringList) {
            String[] split = s.split(COLON);
            ServiceEntry serviceEntry = ServiceEntryBuilder.of(serverId, split[0], Integer.parseInt(split[1]));
            resultList.add(serviceEntry);
        }
        return resultList;
    }

    private static List<ServiceEntry> mergeServiceEntryList(List<ServiceEntry> args1, List<ServiceEntry> args2){
        if(CollectionUtil.isEmpty(args1) && CollectionUtil.isEmpty(args2)){
            return Lists.newArrayList();
        }

        if(CollectionUtil.isEmpty(args1)){
            return args2;
        }

        if(CollectionUtil.isEmpty(args2)){
            return args1;
        }

        // Arrays.asList()操作过的数组,得到的list是只读的,调用add(),remove()方法实际是调用的abstracList中的方法
        // 所以这里报错请注意
        args1.addAll(args2);

        // 过滤掉相同的服务
        return args1.stream().collect(Collectors.collectingAndThen(
                Collectors.toCollection(()->
                        new TreeSet<>(Comparator.comparing(
                                // 多条件
                                s->s.port()+s.ip()
                        ))
                ),ArrayList::new

        ));
    }



}
