# e-rpc
e-prc是基于netty4 实现的 java rpc 远程调用框架。

一个定制化轻量级高效的rpc框架。

代码实现功能，主要参考 <a href="https://dubbo.apache.org/zh/docs/introduction/" target="_blank">apache dubbo</a>, <a href="https://github.com/houbb/rpc" target="_blank">houbb/rpc</a>

___

* 基于 netty4 的客户端调用服务端

* protostuff、object等多种编码解码

* timeout 超时处理

* 本地注册中心、zookeeper、redis注册中心

* load balance 负载均衡

* callType 支持 oneway sync 等调用方式

* fail 支持 failOver failFast 等失败处理策略

* generic 支持泛化调用

* gracefully 优雅关闭

* rpcInterceptor 拦截器

* filter 过滤器

* heartbeat 服务端心跳

* springboot:2.6.1 整合

# springboot快速入门


## 构建项目测试
___
### 项目结构
```java
e-prc-test-project
├── srpring-boot-server
│   └── src
│       └── com.zou.springbootserver
            ├── HelloServiceImpl.java  
│           ├── SrpringBootServerApplication.java
├── srpring-boot-client
│   └── src      
│       └── com.zou.springbootclient
│           ├── SrpringBootClientApplication.java
├── test-api
│   └── src
│       └── com.zou
│           ├── HelloService.java


```
___
### 创建公共api模块 test-api ，并创建HelloService接口
```java
public interface HelloService {

     String sayHello();

}
```
___
### 创建服务提供者模块 spring-boot-server ，并创建HelloService接口的实现类
```java
/**
 * 被@ERpcService标注的类会自动加入spring容器，并且暴露服务。
 */
@ERpcService
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello() {
        return "hello";
    }
}

```
### spring-boot-server 的pom.xml
___

```xml

<!--rpc依赖-->
 <dependency>
    <groupId>io.github.zoujunjienb</groupId>
    <artifactId>rpc-all</artifactId>
    <version>1.0.4-RELEASE</version>
</dependency>

<!-- 公共api接口模块-->
<dependency>
    <groupId>com.zou</groupId>
    <artifactId>test-api</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```
### application.properties, 如果是yml格式变下，属性一样
```properties
# rpc 服务端口
e-rpc.application.server.port=8022
# 注册中心的地址:,集群：127.0.0.1:2181,127.0.0.1:2182,...
e-rpc.application.register.url=127.0.0.1:2181
# 注册中心类型,目前支持: zookeeper、redis
e-rpc.application.register.type=zookeeper
# 注册中心密码，没有可不填
e-rpc.application.register.password=123456
```
___
### 创建服务消费者模块 spring-boot-client ，并创建启动类
```java
/**
 * @EnableERpc 开启rpc服务。扫描该类下所有包含 @ERpcReference注解的属性
 */
@SpringBootApplication
@RestController
@EnableERpc
public class SrpringBootClientApplication {

    /**
     * ERpcReference
     */
    @ERpcReference
    private HelloService helloService;

    @GetMapping("hello")
    public String hello(){
        return helloService.sayHello();
    }

    public static void main(String[] args) {
        SpringApplication.run(SrpringBootClientApplication.class, args);
    }

}


```

### spring-boot-client 的pom.xml
___

```xml

<!--rpc依赖-->
 <dependency>
    <groupId>io.github.zoujunjienb</groupId>
    <artifactId>rpc-all</artifactId>
    <version>1.0.4-RELEASE</version>
</dependency>

<!-- 公共api接口模块-->
<dependency>
    <groupId>com.zou</groupId>
    <artifactId>test-api</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>


<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```
### application.properties, 如果是yml格式变下，属性一样
```properties
# rpc 服务端口
e-rpc.application.server.port=8021
# 注册中心的地址:,集群：127.0.0.1:2181,127.0.0.1:2182,...
e-rpc.application.register.url=127.0.0.1:2181
# 注册中心类型,目前支持: zookeeper、redis
e-rpc.application.register.type=zookeeper
# 注册中心密码，没有可不填
e-rpc.application.register.password=123456
```

