# Swagger Kubernetes

## 一、简介

Swagger Kubernetes 是一款在 Kubernetes 集群中对使用 Swagger 规范的服务 API 进行文档聚合的项目。可以自动 Kubernetes 某个 Namespace 下的服务发现,将集成 Swagger API 规范的接口过滤,通过 zuul 反向代理将服务代理到该服务下，通过 Swagger UI 来将 Swagger API 信息聚合显示。

Swagger kubernetes 版本 | Springboot 版本 | 对应 swagger 版本
---|---|---
2.1.0 | 2.1.x | 2.0

## 二、可配置参数

### Swagger 文档全局配置

参数名称|默认值|描述
---|---|---
swagger.global.swagger-version | 2.0 | 设置全局文档的标识版本号
swagger.global.doc-api-path | /v2/api-docs | 设置 Swagger Api 接口路径
swagger.global.ignore-services| - | 忽略的服务列表，例如：service1,service2,service3,.....

### Zuul 配置

参数名称|默认值|描述
---|---|---
zuul.routes.<serviceId>.url| - | 静态路由的 url 地址,其中 serviceId 跟 kubernetes service 名对应,值填写为 kubernetes 集群地址，例如：zuul.routes.kubernetesServiceName.url=http：//kubernetesIP:port
zuul.routes.<serviceId>.path | - | url 的 path,zuul代理到本地的路径,其中 serviceId 跟 kubernetes service 名对应,一般也设置为 kubernetes service 名,例如：/serviceName/**

### Kubernetes 集群连接配置

- **通过 Token 连接 Kubernetes**

参数名称|默认值|描述
---|---|---
swagger.kubernetes.connect.url | - | kubernetes 集群地址
swagger.kubernetes.connect.token | - | kubernetes 连接时候的 token 字符串
swagger.kubernetes.connect.tokenPath | - | kubernetes token 文件的地址,如果参数中未设置 toekn 则从 token 文件中读取token字符串,例如：d:\\token.txt
swagger.kubernetes.connect.validateSsl | false | 是否验证 ssl
swagger.kubernetes.connect.caPath | - | ca 证书地址（首先设置 validateSsl 为 true）

- **通过 Config 配置文件连接 Kubernetes**

参数名称|默认值|描述
---|---|---
swagger.kubernetes.connect.fromConfigPath | - | d:\\config
swagger.kubernetes.connect.fromDefault | true | 如果未设置 fromConfigPath 参数,则默认从 "/$HOME/.kube/config" 查找配置文件

- **通过 Kubernetes Cluster 环境连接 Kubernetes**

参数名称|默认值|描述
---|---|---
swagger.kubernetes.connect.fromCluster | true | 如果未配置token与config文件,则会检测程序是否运行在 Kubernetes 集群中，如果是则会利用集群中给Pod分配的服务账户连接 kubernetes。

### Discovery 设置

- **时间配置**

参数名称|默认值|描述
---|---|---
swagger.discovery.initial-delay |  30 | 初始化执行服务获取定时任务的懒加载时间
swagger.discovery.interval | 30 | 定时周期获取 kubernetes 服务列表的时间

- **Namespace & Port Type 设置**

参数名称|默认值|描述
---|---|---
swagger.discovery.url | - | 服务发现的地址,一般设置为kubernetes集群地址，也可以设置为 Ingress 地址,例如：http://ClusterIP
swagger.discovery.namespace | 当前服务在 Kubernetes 集群的 Namespace | 设置 Kubernetes 集群服务发现的 Namespace 
swagger.discovery.portType | ClusterIP | 服务发现端口类型，可以设置为 ClusterIP、NodePort

## 三、推荐配置

### 在 Kubernetes 集群内启动该服务

如果在 kubernetes 集群内启动该服务,则会利用集群中给该服务分配的 ServiceAccount 连接 Kubernetes 集群,并利用该 ServiceAccount 所拥有的权限发现当前所在的 Namespace 下的所有服务,默认使用 ClusterIP 方式的 Port 获取各个服务的 Swagger API 信息。

application.properties 如下配置：

```properties
... 默认配置即可~
```

### 在 kubernets 集群外启动该服务

如果在 kubernetes 集群外启动该服务,则可以利用 Token 或者集群 Config 文件来连接 Kubernetes 集群,利用 Token 绑定的 ServiceAccount 的权限服务发现指定 Namespace 下的所有服务,利用 NodePort 方式访问服务的 Swagger API 信息。

例如我的Kubernetes 集群为"192.168.2.11",端口为 "6443",则 application.properties 可以如下配置：

- **利用 token 连接 Kubernetes**

```properties
#kubernetes 连接设置
swagger.kubernetes.connect.url=https://192.168.2.11:6443
swagger.kubernetes.connect.token=eyJhbGciOiJSUzI1NiIsImtpZCI6IiJ9.eyJpc3MiOiJrdWJlcm5ldGVz......(略)

#设置不从集群环境连接 Kubernetes
swagger.kubernetes.connect.fromCluster=false 

#服务发现配置
swagger.discovery.url=http://192.168.2.11
swagger.discovery.namespace=kube-public
swagger.discovery.portType=NodePort
```

- **利用 config 连接kubernetes 集群**

```properties
#连接 kubernetes 集群的 config 文件地址，如果 config 文件存放于指定地址，则可以按下配置。如果不配置则默认到 "/$HOME/.kube/config" 查找配置文件
swagger.kubernetes.connect.fromConfigPath=d:\\config

#设置不从集群环境连接 Kubernetes
swagger.kubernetes.connect.fromCluster=false 

#服务发现配置
swagger.discovery.url=http://192.168.2.11
swagger.discovery.namespace=kube-public
swagger.discovery.portType=NodePort
```

## 四、使用示例

### 1、Kubernetes 服务引入 Swagger

设置在 Kubernetes 启动的服务引用 Swagger 相关 jar 来暴露 Swagger Api 方便 “Swagger Kubernetes” 收集这个暴露的 Swagger API 信息，整理服务接口。 

#### Maven 引入 springfox-swagger2

```xml
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger2</artifactId>
    <version>${version.swagger}</version>
</dependency>
```

### 2、Kubernetes 文档聚合服务引入 Swagger Kubernetes

#### 创建 SpringBoot 项目

关于如何创建 SpringBoot 服务例子较多，这里不过多叙述，不过由于兼容性，请使用 2.1.X 版本的 SpringBoot。

#### Maven 引入 Swagger Kubernetes

```xml
<dependency>
    <groupId>club.mydlq</groupId>
    <artifactId>swagger-kubernetes</artifactId>
    <version>2.1.0</version>
</dependency>
```

#### SpringBoot 启动类加注解开启 Swagger Kubernetes

```java
import club.mydlq.swagger.kubernetes.EnableSwaggerKubernetes;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// 开启 Swagger Kubernetes
@EnableSwaggerKubernetes
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
```

#### 将该项目编译 Docker 镜像并发布到 Kubernetes

（略）


