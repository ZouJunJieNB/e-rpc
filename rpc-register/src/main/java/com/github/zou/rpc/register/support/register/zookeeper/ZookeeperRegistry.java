package com.github.zou.rpc.register.support.register.zookeeper;

import com.github.houbb.heaven.util.lang.StringUtil;
import com.github.houbb.heaven.util.util.JsonUtil;
import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;
import com.github.zou.rpc.common.exception.RpcRuntimeException;
import com.github.zou.rpc.register.support.register.ExtendRegistry;
import com.github.zou.rpc.register.support.register.URL;
import com.github.zou.rpc.common.domain.entry.ServiceEntry;
import com.github.zou.rpc.register.simple.client.RegisterClientService;
import com.github.zou.rpc.register.simple.server.RegisterServerService;
import com.github.zou.rpc.register.support.register.FailBackRegistry;
import com.github.zou.rpc.register.support.register.zookeeper.remoting.CuratorZookeeperClient;
import com.github.zou.rpc.register.support.register.zookeeper.remoting.ZookeeperClient;
import io.netty.channel.Channel;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

import static com.github.zou.rpc.common.constant.PunctuationConst.*;


/**
 * ZookeeperRegistry
 * @author zou
 * @since 1.0.0
 */
public class ZookeeperRegistry extends ExtendRegistry {

    private static final Log log = LogFactory.getLog(ZookeeperRegistry.class);

    private final static int DEFAULT_ZOOKEEPER_PORT = 2181;

    private final static String DEFAULT_ROOT = "e-rpc";


    private final ZookeeperClient zkClient;


    public ZookeeperRegistry(URL url,RegisterServerService registerServerService, RegisterClientService registerClientService) {
        super(registerServerService, registerClientService);
        url.isCorrectThrow();
        String group = url.getGroup( DEFAULT_ROOT);
        if (!group.startsWith(SLASH)) {
            group = SLASH + group;
        }
        super.setRoot(group);

        zkClient = new CuratorZookeeperClient(url);

    }

    @Override
    public List<ServiceEntry> doLookUp(String serviceId) {
        List<String> children = zkClient.getChildren(toRootDir() + server() + SLASH + serviceId);
        return  convertServiceEntry(children,serviceId);
    }

    @Override
    public void doRegister(ServiceEntry serviceEntry, Channel channel) {
        try {
            zkClient.create(toUrlPath(serviceEntry,server()),JSONObject.toJSONString(channel) ,serviceEntry.temporary());
        } catch (Throwable e) {
            throw new RpcRuntimeException("Failed to register " + serviceEntry + " to zookeeper " + serviceEntry.ip() + ", cause: " + e.getMessage(), e);
        }
    }

    @Override
    public void doUnRegister(ServiceEntry serviceEntry) {
        try {
            zkClient.delete(toUrlPath(serviceEntry,server()));
        } catch (Throwable e) {
            throw new RpcRuntimeException("Failed to unregister " + serviceEntry + " to zookeeper " + serviceEntry.ip() + ", cause: " + e.getMessage(), e);
        }
    }

    @Override
    public void doSubscribe(ServiceEntry serviceEntry, Channel channel) {
        try {
            zkClient.create(toUrlPath(serviceEntry,client())+SLASH+channel.id(),JSONObject.toJSONString(channel) ,serviceEntry.temporary());
        } catch (Throwable e) {
            throw new RpcRuntimeException("Failed to doSubscribe " + serviceEntry + " to zookeeper " + serviceEntry.ip() + ", cause: " + e.getMessage(), e);
        }
    }

    @Override
    public void doUnSubscribe(ServiceEntry serviceEntry, Channel channel) {
        try {
            zkClient.delete(toUrlPath(serviceEntry,client())+SLASH+channel.id());
        } catch (Throwable e) {
            throw new RpcRuntimeException("Failed to doUnSubscribe " + serviceEntry + " to zookeeper " + serviceEntry.ip() + ", cause: " + e.getMessage(), e);
        }
    }



}
