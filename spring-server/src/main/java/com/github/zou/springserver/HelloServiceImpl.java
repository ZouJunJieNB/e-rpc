package com.github.zou.springserver;


import com.github.zou.HelloService;
import com.github.zou.config.spring.beans.factory.annotation.context.annotation.ERpcService;

/**
 * @author zou
 * @since 1.0.0
 */
@ERpcService
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello() {
        return "hello";
    }
}
