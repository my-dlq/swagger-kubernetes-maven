# Swagger Kubernetes

## 简介

Swagger Kubernetes 是一款在 Kubernetes 集群中对使用 Swagger 规范的服务 API 进行文档聚合的项目。可以自动 Kubernetes 某个 Namespace 下的服务发现,将集成 Swagger API 规范的接口过滤,通过 zuul 反向代理将服务代理到该服务下，通过 Swagger UI 来将 Swagger API 信息聚合显示。

Swagger kubernetes 版本 | Springboot 版本 | 对应 swagger 版本
---|---|---
2.1.0 | 2.1.x | 2.0


## SpringBoot使用示例

[SpringBoot 项目使用 Swagger 示例](https://github.com/my-dlq/swagger-kubernetes/tree/master/example/swagger-example-service)

[Swagger Kubernetes 源码地址](https://github.com/my-dlq/swagger-kubernetes/tree/master/swagger-kubernentes-core)

[SpringBoot 项目使用 Swagger Kubernetes 示例](https://github.com/my-dlq/swagger-kubernetes/tree/master/example/swagger-kubernetes-example-service)

## Swagger Kubernetes Docker 项目

[Kubernetes 中创建 Swagger Kubernetes Docker 服务](https://github.com/my-dlq/swagger-kubernetes/tree/master/swagger-kubernetes-docker)