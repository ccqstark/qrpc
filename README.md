<p align="center">
  <img width="320" src="https://cdn.jsdelivr.net/gh/ccqstark/image-bed/images/202207162356036.png">
</p>

<p align="center">
  <a>
    <img src="https://img.shields.io/github/languages/code-size/ccqstark/qrpc">
  </a>
  <a>
    <img src="https://img.shields.io/github/license/ccqstark/qrpc">
  </a>
</p>

### 简介

一个基于 Netty + Kryo + ZooKeeper 的 RPC 框架。

本项目是参考[Snailclimb/guide-rpc-framework](https://github.com/Snailclimb/guide-rpc-framework)实现的，添加了自己对代码理解的注释，并且后续会增加一些原本没有的新特性。

> 本项目仅供学习RPC原理使用。



### 下载

```xml
<dependency>
		<groupId>io.github.ccqstark</groupId>
  	<artifactId>qrpc-core</artifactId>
  	<version>1.0.0</version>
</dependency>
```





### 使用示例







### 功能计划

- [ ] Spring Boot starter 支持
- [ ] 注册中心 Nacos 支持
- [ ] 多种序列化实现
- [ ] 类似dubbo-admin的后台可视化管理系统
- [ ] 时间轮算法实现定时任务（客户端调用超时、心跳）

