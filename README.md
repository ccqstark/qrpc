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
  <a>
    <img src="https://img.shields.io/github/commit-status/ccqstark/qrpc/master/d4b1bf9bece7e6b0497509c9064336722dc36e4a">
  </a>
  <a>
    <img src="https://img.shields.io/maven-central/v/io.github.ccqstark/qrpc">
  </a>
</p>


### 简介

一个基于 Netty + Kryo + ZooKeeper 的 RPC 框架。

本项目是参考[Snailclimb/guide-rpc-framework](https://github.com/Snailclimb/guide-rpc-framework)实现的，添加了自己对代码理解的注释，并且后续会增加一些原本没有的新特性。

> 本项目仅供学习RPC原理使用。



### 下载

```xml
<!-- https://mvnrepository.com/artifact/io.github.ccqstark/qrpc-core -->
<dependency>
    <groupId>io.github.ccqstark</groupId>
    <artifactId>qrpc-core</artifactId>
    <version>1.0.0</version>
</dependency>
```



### 使用示例

#### 配置zookeeper地址

在resources目录下创建`qrpc.properties`

```properties
qrpc.zookeeper.address=127.0.0.1:2181
```

#### 服务接口

```java
public interface AddService {
    int add(int a, int b);
}
```

#### 服务实现

```java
// 通过注解注册服务
@RpcService(group = "math", version = "1.0.0")
public class AddServiceImpl implements AddService {
    public int add(int a, int b) {
        return a + b;
    }
}
```

`@RpcService`注解用于服务实现类上，`group`用于业务分组，`version`为服务版本号

#### 服务端

```java
@RpcScan(basePackage = {"com.ccqstark.qrpctestserver.service"})
public class NettyServerMain {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(NettyServerMain.class);
        NettyRpcServer nettyRpcServer = (NettyRpcServer) applicationContext.getBean("nettyRpcServer");
        // 手动注册服务
        AddService addService = new AddServiceImpl();
        RpcServiceConfig rpcServiceConfig = RpcServiceConfig.builder()
                .group("math").version("1.0.0").service(addService).build();
        nettyRpcServer.registerService(rpcServiceConfig);
        nettyRpcServer.start();
    }
}
```

`@RpcScan`的**basePackage**为服务实现的包路径（服务端），该路径下的含有`@RpcService`注解的类都会自动注册为服务。

也可以如上代码实例中那样去手动注册服务。

#### 客户端

客户端启动类中同样用`@RpcScan`的**basePackage**，不过指定的是注入了远程服务的类所在的路径

注入服务使用`@RpcReference`，如下所示：

```java
@RpcReference(group = "math", version = "1.0.0")
private AddService addService;
```

注入成功后就可以调用远程服务中的方法了。



### 功能计划

- [x] 发布到Maven中央仓库
- [x] 多种序列化实现
- [ ] Spring Boot starter 支持
- [ ] 注册中心 Nacos 支持
- [ ] 类似dubbo-admin的后台可视化管理系统
- [ ] 时间轮算法实现定时任务（客户端调用超时、心跳）
