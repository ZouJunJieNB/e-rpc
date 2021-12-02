package com.github.zou.rpc.common.support.resource;

import com.github.zou.rpc.common.api.Destroyable;

/**
 *
 * @author zou
 * @since 1.0.0
 */
public interface ResourceManager {

    /**
     * 新增可销毁的资源信息
     * @param destroyable 可销毁的资源信息
     * @return this
     * @since 0.1.3
     */
    ResourceManager addDestroy(final Destroyable destroyable);

    /**
     * 销毁所有资源
     * （1）销毁所有的列表资源
     * （2）清空可销毁的列表
     * @return this
     * @since 0.1.3
     */
    ResourceManager destroyAll();

}
