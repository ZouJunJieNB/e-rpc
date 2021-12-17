package com.github.zou.rpc.common.util;

import com.github.zou.rpc.common.exception.RpcRuntimeException;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import static com.github.zou.rpc.common.constant.PunctuationConst.COLON;

/**
 * IP 工具類
 * @since 0.2.0
 */
public final class IpUtils {

    /**
     * 构建对应的 ip:port 结果
     * @param ip 地址
     * @param port 端口
     * @return 结果
     * @since 0.2.0
     */
    public static String ipPort(String ip, int port) {
        return ip+":"+port;
    }

    private static int registerPort;

    static {
        try {
            //  当传入端口号为0时，系统将会随机分配一个空闲的端口号给你
            registerPort = new DatagramSocket(0).getLocalPort();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    /**
     * 注册中心默认端口
     * @return
     */
    public static int registerPort(){
        return registerPort;
    }

    /**
     * 获取注册中心默认
     * @return
     */
    public static String registerAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress()+COLON+registerPort();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            throw new RpcRuntimeException("get address error");
        }

    }

}
