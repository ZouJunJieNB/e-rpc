package com.github.zou.rpc.register.support.register.redis;

import com.alibaba.fastjson.JSONObject;
import com.github.houbb.heaven.util.lang.StringUtil;
import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;
import com.github.zou.rpc.common.domain.entry.ServiceEntry;
import com.github.zou.rpc.common.exception.RpcRuntimeException;
import com.github.zou.rpc.register.simple.client.RegisterClientService;
import com.github.zou.rpc.register.simple.server.RegisterServerService;
import com.github.zou.rpc.register.support.register.AbstractRpcRegistry;
import com.github.zou.rpc.register.support.register.ExtendRegistry;
import com.github.zou.rpc.register.support.register.FailBackRegistry;
import com.github.zou.rpc.register.support.register.URL;
import io.netty.channel.Channel;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.github.zou.rpc.common.constant.PunctuationConst.*;
import static redis.clients.jedis.Protocol.DEFAULT_TIMEOUT;

/**
 * @author zou
 * @since 1.0.0
 */
public class RedisRegistry extends ExtendRegistry {

    private static final Log log = LogFactory.getLog(RedisRegistry.class);

    private static final int DEFAULT_REDIS_PORT = 6379;

    private final static String DEFAULT_ROOT = "e-rpc";

    private final Map<String, JedisPool> jedisPools = new ConcurrentHashMap<>();

    // todo 这里默认都是复制操作
    private boolean replicate = true;

    public RedisRegistry(URL url, RegisterServerService registerServerService, RegisterClientService registerClientService) {
        super(registerServerService, registerClientService);
        url.isCorrectThrow();
        String group = url.getGroup(DEFAULT_ROOT);

        if (!group.startsWith(SLASH)) {
            group = SLASH + group;
        }
        if (!group.endsWith(SLASH)) {
            group = group + SLASH;
        }
        super.setRoot(group);

        String[] addresses = url.getAddress().split(COMMA);
        for (String address : addresses) {
            int i = address.indexOf(COLON);
            String host;
            int port;
            if (i > 0) {
                host = address.substring(0, i);
                port = Integer.parseInt(address.substring(i + 1));
            } else {
                host = address;
                port = DEFAULT_REDIS_PORT;
            }
            // todo db.index 后期融入url当中
            this.jedisPools.put(address, new JedisPool(new JedisPoolConfig(), host, port,
                    url.getTimeout(), StringUtil.isEmpty(url.getPassword()) ? null : url.getPassword(),
                     0));

        }
    }

    @Override
    public List<ServiceEntry> doLookUp(String serviceId) {

        boolean success = false;
        RpcRuntimeException exception = null;
        Set<String> address = new HashSet<>();
        for (Map.Entry<String, JedisPool> entry : jedisPools.entrySet()) {

            JedisPool jedisPool = entry.getValue();
            try {
                try (Jedis jedis = jedisPool.getResource()) {
                    Map<String, String> stringStringMap = jedis.hgetAll(toKeyPath(serviceId, server()));
                    for (Map.Entry<String, String> stringStringEntry : stringStringMap.entrySet()) {
                        address.add(stringStringEntry.getKey());
                    }
                    success = true;
                    if (!replicate) {
                        break; //  If the server side has synchronized data, just write a single machine
                    }
                }
            } catch (Throwable t) {
                exception = new RpcRuntimeException("Failed to lookUp service to redis registry. registry: " + entry.getKey() + ", serviceId: " + serviceId + ", cause: " + t.getMessage(), t);
            }
        }

        // 如果一个都没成功直接抛异常
        if (exception != null) {
            if (!success) {
                throw exception;
            }
            log.warn(exception.getMessage(), exception);
        }

        return convertServiceEntry(address,serviceId);
    }

    @Override
    public void doRegister(ServiceEntry serviceEntry, Channel channel) {
        String key = toKeyPath(serviceEntry.serviceId(),server());

        String value = splicingNodeNoSlash(serviceEntry);

        boolean success = false;
        RpcRuntimeException exception = null;

        for (Map.Entry<String, JedisPool> entry : jedisPools.entrySet()) {
            JedisPool jedisPool = entry.getValue();
            try {
                try (Jedis jedis = jedisPool.getResource()) {
                    jedis.hset(key, value,JSONObject.toJSONString(channel));


                    //  这里不进行订阅操作
//                    jedis.publish(key, REGISTER);
                    success = true;
                    if (!replicate) {
                        break; //  If the server side has synchronized data, just write a single machine
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
                exception = new RpcRuntimeException("Failed to register service to redis registry. registry: " + entry.getKey() + ", service: " + serviceEntry + ", cause: " + t.getMessage(), t);
            }
        }

        // 如果一个都没成功直接抛异常
        if (exception != null) {
            if (!success) {
                throw exception;
            }
            log.warn(exception.getMessage(), exception);
        }
    }

    @Override
    public void doUnRegister(ServiceEntry serviceEntry) {
        String key = toKeyPath(serviceEntry.serviceId(),server());

        String value = splicingNodeNoSlash(serviceEntry);


        boolean success = false;
        RpcRuntimeException exception = null;

        for (Map.Entry<String, JedisPool> entry : jedisPools.entrySet()) {
            JedisPool jedisPool = entry.getValue();
            try {
                try (Jedis jedis = jedisPool.getResource()) {
                    jedis.hdel(key, value);

                    // 这里不推送消息
//                    jedis.publish(key, UNREGISTER);
                    success = true;
                    if (!replicate) {
                        break; //  If the server side has synchronized data, just write a single machine
                    }
                }
            } catch (Throwable t) {
                exception = new RpcRuntimeException("Failed to unregister service to redis registry. registry: " + entry.getKey() + ", service: " + serviceEntry + ", cause: " + t.getMessage(), t);
            }
        }
        if (exception != null) {
            if (!success) {
                throw exception;
            }
            log.warn(exception.getMessage(), exception);
        }
    }

    @Override
    public void doSubscribe(ServiceEntry serviceEntry, Channel channel) {
        String key = toKeyPath(serviceEntry.serviceId(),client());
        String value = channel.id().toString();

        boolean success = false;
        RpcRuntimeException exception = null;

        for (Map.Entry<String, JedisPool> entry : jedisPools.entrySet()) {
            JedisPool jedisPool = entry.getValue();
            try {
                try (Jedis jedis = jedisPool.getResource()) {
                    jedis.hset(key, value,   JSONObject.toJSONString(channel));

                    // 这里不推送消息
//                    jedis.publish(key, UNREGISTER);
                    success = true;
                    if (!replicate) {
                        break; //  If the server side has synchronized data, just write a single machine
                    }
                }
            } catch (Throwable t) {
                exception = new RpcRuntimeException("Failed to subscribe service to redis registry. registry: " + entry.getKey() + ", service: " + serviceEntry + ", cause: " + t.getMessage(), t);
            }
        }
        if (exception != null) {
            if (!success) {
                throw exception;
            }
            log.warn(exception.getMessage(), exception);
        }

    }

    @Override
    public void doUnSubscribe(ServiceEntry serviceEntry, Channel channel) {
        String key = toKeyPath(serviceEntry.serviceId(),client());

        String value = channel.id().toString();


        boolean success = false;
        RpcRuntimeException exception = null;

        for (Map.Entry<String, JedisPool> entry : jedisPools.entrySet()) {
            JedisPool jedisPool = entry.getValue();
            try {
                try (Jedis jedis = jedisPool.getResource()) {
                    jedis.hdel(key, value);

                    // 这里不推送消息
//                    jedis.publish(key, UNREGISTER);
                    success = true;
                    if (!replicate) {
                        break; //  If the server side has synchronized data, just write a single machine
                    }
                }
            } catch (Throwable t) {
                exception = new RpcRuntimeException("Failed to unSubscribe service to redis registry. registry: " + entry.getKey() + ", service: " + serviceEntry + ", cause: " + t.getMessage(), t);
            }
        }
        if (exception != null) {
            if (!success) {
                throw exception;
            }
            log.warn(exception.getMessage(), exception);
        }
    }





}
