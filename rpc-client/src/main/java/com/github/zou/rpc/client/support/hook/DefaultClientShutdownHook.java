package com.github.zou.rpc.client.support.hook;

import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;
import com.github.zou.rpc.client.support.register.ClientRegisterManager;
import com.github.zou.rpc.common.support.hook.AbstractShutdownHook;
import com.github.zou.rpc.common.support.invoke.InvokeManager;
import com.github.zou.rpc.common.support.resource.ResourceManager;
import com.github.zou.rpc.common.support.status.enums.StatusEnum;
import com.github.zou.rpc.common.support.status.service.StatusManager;
import com.github.zou.rpc.common.util.Waits;

/**
 * 默认的客户端 hook 实现
 * <p> project: rpc-ClientShutdownHook </p>
 * <p> create on 2019/10/30 20:26 </p>
 *
 * @author Administrator
 * @since 0.1.8
 */
public class DefaultClientShutdownHook extends AbstractShutdownHook {

    /**
     * DefaultShutdownHook logger
     */
    private static final Log LOG = LogFactory.getLog(DefaultClientShutdownHook.class);

    /**
     * 状态管理类
     * @since 0.1.3
     */
    private StatusManager statusManager;

    /**
     * 调用管理类
     * @since 0.1.3
     */
    private InvokeManager invokeManager;

    /**
     * 资源管理类
     * @since 0.1.3
     */
    private ResourceManager resourceManager;

    /**
     * 客户端注册中心管理类
     * @since 0.1.8
     */
    private ClientRegisterManager clientRegisterManager;

    /**
     * 为剩余的请求等待时间
     * @since 0.1.8
     */
    private long waitMillsForRemainRequest = 60 * 1000;

    public static DefaultClientShutdownHook newInstance() {
        return new DefaultClientShutdownHook();
    }

    public DefaultClientShutdownHook clientRegisterManager(ClientRegisterManager clientRegisterManager) {
        this.clientRegisterManager = clientRegisterManager;
        return this;
    }

    public DefaultClientShutdownHook statusManager(StatusManager statusManager) {
        this.statusManager = statusManager;
        return this;
    }

    public DefaultClientShutdownHook invokeManager(InvokeManager invokeManager) {
        this.invokeManager = invokeManager;
        return this;
    }

    public DefaultClientShutdownHook resourceManager(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
        return this;
    }

    public DefaultClientShutdownHook waitMillsForRemainRequest(long waitMillsForRemainRequest) {
        this.waitMillsForRemainRequest = waitMillsForRemainRequest;
        return this;
    }

    /**
     * （1）设置 status 状态为等待关闭
     * （2）查看是否 {@link InvokeManager#remainsRequest()} 是否包含请求
     * （3）超时检测-可以不添加，如果难以关闭成功，直接强制关闭即可。
     * （4）关闭所有线程池资源信息
     * （5）设置状态为成功关闭
     */
    @Override
    protected void doHook() {
        // 设置状态为等待关闭
        statusManager.status(StatusEnum.WAIT_SHUTDOWN.code());
        LOG.info("[Shutdown] set status to wait for shutdown.");

        // 通知取消所有服务端的订阅
        clientRegisterManager.unSubscribeServerAll();

        // 循环等待当前执行的请求执行完成
        long startMills = System.currentTimeMillis();
        while (invokeManager.remainsRequest()) {
            long currentMills = System.currentTimeMillis();
            long costMills = currentMills - startMills;
            if(costMills >= waitMillsForRemainRequest) {
                LOG.warn("[Shutdown] still remains request, but timeout, break.");
                break;
            }

            LOG.info("[Shutdown] still remains request, wait for a while.");
            Waits.waits(5);
        }

        // 销毁所有资源
        LOG.info("[Shutdown] resourceManager start destroy all resources.");
        this.resourceManager.destroyAll();
        LOG.info("[Shutdown] resourceManager finish destroy all resources.");

        // 设置状态为关闭成功
        statusManager.status(StatusEnum.SHUTDOWN_SUCCESS.code());
        LOG.info("[Shutdown] set status to shutdown success.");
    }

}
