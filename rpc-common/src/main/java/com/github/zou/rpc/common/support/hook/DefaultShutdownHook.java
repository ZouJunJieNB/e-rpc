package com.github.zou.rpc.common.support.hook;

import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;
import com.github.zou.rpc.common.support.invoke.InvokeManager;
import com.github.zou.rpc.common.support.resource.ResourceManager;
import com.github.zou.rpc.common.support.status.enums.StatusEnum;
import com.github.zou.rpc.common.support.status.service.StatusManager;
import com.github.zou.rpc.common.util.Waits;

/**
 * 默认的 hook 实现
 * <p> project: rpc-ClientShutdownHook </p>
 * <p> create on 2019/10/30 20:26 </p>
 *
 * @author Administrator
 * @since 0.1.3
 */
@Deprecated
public class DefaultShutdownHook extends AbstractShutdownHook {

    /**
     * DefaultShutdownHook logger
     */
    private static final Log LOG = LogFactory.getLog(DefaultShutdownHook.class);

    /**
     * 状态管理类
     * @since 0.1.3
     */
    private final StatusManager statusManager;

    /**
     * 调用管理类
     * @since 0.1.3
     */
    private final InvokeManager invokeManager;

    /**
     * 资源管理类
     * @since 0.1.3
     */
    private final ResourceManager resourceManager;

    public DefaultShutdownHook(StatusManager statusManager, InvokeManager invokeManager, ResourceManager resourceManager) {
        this.statusManager = statusManager;
        this.invokeManager = invokeManager;
        this.resourceManager = resourceManager;
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

        // 循环等待当前执行的请求执行完成
        while (invokeManager.remainsRequest()) {
            LOG.info("[Shutdown] still remains request, wait for a while.");
            Waits.waits(10);
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
