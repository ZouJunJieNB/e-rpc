
package com.github.zou.rpc.common.config.component;

import com.github.houbb.heaven.constant.PunctuationConst;
import com.github.houbb.heaven.util.common.ArgUtil;
import com.github.houbb.heaven.util.guava.Guavas;
import com.github.houbb.heaven.util.lang.NumUtil;

import java.util.List;

/**
 * 地址字符串解析转为Address工具类
 * @author zou
 * @since 1.0.0
 */
public final class RpcAddressBuilder {

    private RpcAddressBuilder(){}

    /**
     * 地址字符串解析成RpcAddress集合
     * @param addresses 地址信息
     * @return 转换后的信息列表
     * @since 1.0.0
     */
    public static List<RpcAddress> of(final String addresses) {
        ArgUtil.notEmpty(addresses, "addresses");

        String[] addressArray = addresses.split(PunctuationConst.COMMA);
        ArgUtil.notEmpty(addressArray, "addresses");

        List<RpcAddress> rpcAddressList = Guavas.newArrayList(addressArray.length);
        for(String address : addressArray) {
            String[] addressSplits = address.split(PunctuationConst.COLON);
            if (addressSplits.length < 2) {
                throw new IllegalArgumentException("Address must be has ip and port, like 127.0.0.1:9527");
            }
            String ip = addressSplits[0];
            int port = NumUtil.toIntegerThrows(addressSplits[1]);
            // 包含权重信息
            int weight = 1;
            if (addressSplits.length >= 3) {
                weight = NumUtil.toInteger(addressSplits[2], 1);
            }

            RpcAddress rpcAddress = new RpcAddress(ip, port, weight);
            rpcAddressList.add(rpcAddress);
        }
        return rpcAddressList;
    }
}
