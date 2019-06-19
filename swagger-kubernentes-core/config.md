
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
