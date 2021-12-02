package com.github.zou.rpc.common.remote.netty;


import com.github.zou.rpc.common.api.Destroyable;
import com.github.zou.rpc.common.api.Initializable;

import java.util.concurrent.Callable;

/**
 * netty 网络客户端
 * @author zou
 * @since 1.0.0
 * @param <V> 泛型
 */
public interface NettyClient<V> extends Callable<V>, Destroyable, Initializable {
}
