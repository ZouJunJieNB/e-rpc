package com.github.zou.rpc.register.support.register;

import com.github.houbb.heaven.util.lang.StringUtil;
import com.github.zou.rpc.common.util.IpUtils;
import com.github.zou.rpc.register.constant.enums.RegisterTypeEnum;

import java.io.Serializable;

import static com.github.zou.rpc.common.constant.PunctuationConst.COLON;
import static com.github.zou.rpc.common.constant.PunctuationConst.COMMA;

/**
 * 连接URL信息
 * @author zou
 * @since 1.0.0
 */
public final class URL implements Serializable {
    private static final long serialVersionUID = -1985165475234910535L;

    private final RegisterTypeEnum registerTypeEnum;

    private final String username;

    private final String password;

    /**
     * 127.0.0.1:8080,xxx:xxx
     */
    private final String address;

    private String group;

    /**
     * 单位: 毫秒
     */
    private Integer timeout;

    public URL(String address){
        this(address,null);
    }

    public URL(String address,String group){
        this(address,group,null);
    }

    public URL(String address,String group,Integer timeout){
        this(RegisterTypeEnum.DEFAULT,null,null,address,group,timeout);
    }


    public URL(RegisterTypeEnum registerTypeEnum,String username,String password,String address,String group,Integer timeout){
        this.registerTypeEnum = registerTypeEnum;
        this.username = username;
        this.password = password;
        this.address = address;
        this.group = group;
        this.timeout = timeout;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getAddress() {
        return address;
    }

    public void isCorrectThrow(){
        if(!isCorrect()){
            throw new IllegalArgumentException(registerTypeEnum.getName()+" url address format is not correct");
        }
    }

    public boolean isCorrect(){
        if(StringUtil.isEmpty(address)){
            return false;
        }
        boolean isCorrect = true;
        String[] split = address.split(COMMA);
        for (String address : split) {
            String[] hostAndPort = address.split(COLON);
            if(hostAndPort.length <= 1){
                isCorrect = false;
            }
        }
        return isCorrect;
    }

    public RegisterTypeEnum getRegisterTypeEnum() {
        return registerTypeEnum;
    }

    public String getGroup(String str) {
        return StringUtil.isEmpty(group) ? str : group;
    }

    public URL setGroup(String group) {
        this.group = group;
        return this;
    }

    public Integer getTimeout() {
        return (timeout == null || timeout == 0)?5000:timeout ;
    }

    public Integer getTimeout(Integer custom) {
        return (timeout == null || timeout == 0)?custom:timeout ;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public String getAuthority() {
        if (StringUtil.isEmpty(username)
                && StringUtil.isEmpty(password)) {
            return null;
        }
        return (username == null ? "" : username)
                + ":" + (password == null ? "" : password);
    }
}
