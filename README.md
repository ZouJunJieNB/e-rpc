# e-rpc
e-prc是基于netty4 实现的 java rpc 远程调用框架。

致力成为一个中小企业定制化轻量级高效的rpc框架。

代码实现功能，主要参考 <a href="https://dubbo.apache.org/zh/docs/introduction/" target="_blank">apache dubbo</a>, <a href="https://github.com/houbb/rpc" target="_blank">houbb/rpc</a>

___

* 基于 netty4 的客户端调用服务端

* protostuff、object等多种编码解码

* timeout 超时处理

* 本地注册中心、zookeeper注册中心

* load balance 负载均衡

* callType 支持 oneway sync 等调用方式

* fail 支持 failOver failFast 等失败处理策略

* generic 支持泛化调用

* gracefully 优雅关闭

* rpcInterceptor 拦截器

* filter 过滤器

* heartbeat 服务端心跳

* springboot 整合
