package com.github.zou.rpc.common.remote.netty;


import com.github.zou.rpc.common.api.AsyncRunnable;
import com.github.zou.rpc.common.api.Destroyable;
import com.github.zou.rpc.common.api.Initializable;

/**
 * netty 网络服务端
 * @author zou
 * @since 1.0.0
 */
public interface NettyServer extends AsyncRunnable, Runnable, Destroyable, Initializable {
}
