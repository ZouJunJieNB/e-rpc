package com.github.zou.rpc.common.exception;

/**
 * 服务已经关闭的异常
 *
 * @author zou
 * @since 0.1.3
 */
public class ShutdownException extends RuntimeException {

    private static final long serialVersionUID = -3400452586261689911L;

    public ShutdownException() {
    }

    public ShutdownException(String message) {
        super(message);
    }

    public ShutdownException(String message, Throwable cause) {
        super(message, cause);
    }

    public ShutdownException(Throwable cause) {
        super(cause);
    }

    public ShutdownException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
