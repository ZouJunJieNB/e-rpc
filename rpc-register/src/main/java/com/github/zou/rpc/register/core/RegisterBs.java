package com.github.zou.rpc.register.core;

import com.github.houbb.heaven.util.common.ArgUtil;
import com.github.zou.rpc.register.support.register.URL;
import com.github.zou.rpc.common.remote.netty.handler.ChannelHandlers;
import com.github.zou.rpc.common.remote.netty.impl.DefaultNettyServer;
import com.github.zou.rpc.register.api.config.RegisterConfig;
import com.github.zou.rpc.register.constant.enums.RegisterTypeEnum;
import com.github.zou.rpc.register.simple.client.RegisterClientService;
import com.github.zou.rpc.register.simple.client.impl.DefaultRegisterClientService;
import com.github.zou.rpc.register.simple.handler.RegisterCenterServerHandler;
import com.github.zou.rpc.register.simple.server.RegisterServerService;
import com.github.zou.rpc.register.simple.server.impl.DefaultRegisterServerService;
import com.github.zou.rpc.register.spi.RpcRegistry;
import com.github.zou.rpc.register.support.hook.RegisterCenterShutdownHook;
import com.github.zou.rpc.register.support.register.RegistryFactory;
import io.netty.channel.ChannelHandler;

import static com.github.zou.rpc.common.util.IpUtils.registerPort;

/**
 * 默认注册中心配置
 * @author zou
 * @since 1.0.0
 */
public class RegisterBs implements RegisterConfig {

    /**
     * 服务启动端口信息
     * @since 1.0.0
     */
    private int port;

    private URL url;

    private RegisterTypeEnum typeEnum;

    /**
     * 服务端
     */
    private final RegisterServerService registerServerService;
    /**
     * 客户端
     */
    private final RegisterClientService registerClientService;

    private RegisterBs(){
        registerServerService = new DefaultRegisterServerService();
        registerClientService = new DefaultRegisterClientService();
    }

    public static RegisterBs newInstance() {
        RegisterBs registerBs = new RegisterBs();
        registerBs.port(registerPort());
        return registerBs;
    }

    public RegisterBs setUrl(URL url) {
        this.url = url;
        return this;
    }

    public RegisterBs setTypeEnum(RegisterTypeEnum typeEnum) {
        this.typeEnum = typeEnum;
        return this;
    }

    /**
     * 构建简单注册实现类
     * @return 注册实现
     * @since 1.0.0
     */
    private RpcRegistry buildSimpleRpcRegister(RegisterServerService registerServerService, RegisterClientService registerClientService) {
        return new RegistryFactory(url,typeEnum,registerServerService, registerClientService).getRpcRegister();
    }

    @Override
    public RegisterBs port(int port) {
        ArgUtil.notNegative(port, "port");

        this.port = port;
        return this;
    }

    @Override
    public RegisterBs start() {

        ArgUtil.notNull(typeEnum,"typeEnum");
        RpcRegistry rpcRegister = buildSimpleRpcRegister(registerServerService, registerClientService);
        ChannelHandler channelHandler = ChannelHandlers.objectCodecHandler(new RegisterCenterServerHandler(rpcRegister));
        DefaultNettyServer.newInstance(port, channelHandler).asyncRun();

        // 通知对应的服务端和客户端，服务启动。
        // 新增的时候暂时不处理。
        // 暂时注册中心的是无状态的，无法获取到没有访问过的节点。（如果访问过，则客户端肯定已经有对应的信息。）
        // 如果使用 redis/database 等集中式的存储，或者进行数据同步，则有通知的必要性。

        // 添加对应的 shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                new RegisterCenterShutdownHook(registerServerService, registerClientService, port).hook();
            }
        });

        return this;
    }

}
