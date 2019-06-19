**这个项目为一个简单 SpringBoot 服务，其中集成了 Swagger，且设置了相应的 Swagger 配置注解来描述接口信息，方便 Swagger Kubernetes 服务将其聚合，统一展示文档。**

**为了方便，此演示项目所编译的 Docker 已经推送到官方仓库，镜像下拉地址为：mydlq/swagger-example-service:1.0.0**

# Springboot 项目集成 Swagger

> 项目为演示 springboot 中如何集成 swagger

## 一、Maven 引入 Swagger 依赖

创建一个 SpringBoot 项目并 Maven 中引入 Swagger。

```xml
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger2</artifactId>
    <version>2.9.2</version>
</dependency>
```

## 二、配置 Swagger

配置 Swagger ，设置 Swagger 文档要展示的信息。

> 注意：配置中不要设置 “groupName” 参数，否则可能无法文档聚合。

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfig {
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                //.groupName("swagger-example-service") // 项目组名
                .select()                             // 选择那些路径和api会生成document
                .apis(RequestHandlerSelectors.any())  // 对所有api进行监控
                .paths(PathSelectors.any())           // 对所有路径进行监控
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("swagger-example-service")           // 文档标题
                .description("This is a swagger project.")  // 文档描述
                .version("1.0.0")                           // 文档版本
                .build();
    }
}
``` 

## 三、创建 user 实体类

创建一个 User 实体类，方便测试。

```java
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;

public class User {
    @ApiModelProperty(value = "姓名", required = true)
    private String name;
    @ApiModelProperty(value = "性别", required = true)
    private String sex;
    @ApiModelProperty(value = "岁数", required = true)
    private Integer age;
    @ApiModelProperty(value = "生日")
    private Date birthday;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getSex() {    
        return sex;
    }
    public void setSex(String sex) {
        this.sex = sex;
    }
    public Integer getAge() {
        return age;
    }
    public void setAge(Integer age) {
        this.age = age;
    }
    public Date getBirthday() {
        return birthday;
    }
    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }
}
```

## 四、创建示例 Controller 接口

创建一个 Controller 类，且运用 Swagger 注解，将接口信息详细描述。

```java
package mydlq.swagger.example;

import io.swagger.annotations.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(tags = "Example Controller")
public class ExampleController {

    @ApiOperation(value = "获取示例信息", notes = "用 Get 请求发送，获取示例设置的字符串信息。")
    @GetMapping(value = "/getExample")
    public String getExample(
            @ApiParam(value = "输入一个 Key") @RequestParam(value = "key") String key,
            @ApiParam(value = "输入一个 Value", required = true) @RequestParam(value = "value") String value) {
        return "The value you enter is:" + key + "：" + value;
    }


    @ApiOperation(value = "发送示例信息", notes = "Post方法，发送示例信息")
    @PostMapping(value = "/postExample")
    public User postExample(@ApiParam(value = "用户信息") @RequestBody User user) {
        return user;
    }


    @ApiResponses({
            @ApiResponse(code = 200, message = "成功"),
            @ApiResponse(code = 201, message = "被创建"),
            @ApiResponse(code = 401, message = "没有权限访问该服务"),
            @ApiResponse(code = 403, message = "权限不足无法访问该服务"),
            @ApiResponse(code = 404, message = "未发现该微服务"),
            @ApiResponse(code = 500, message = "服务器内部错误")
    })
    @ApiOperation(value = "修改示例信息", notes = "Put方法，修改示例信息")
    @PutMapping(value = "/putExample")
    public ResponseEntity<User> putExample(@ApiParam(value = "用户信息") @RequestBody User user) {
        // 设置 Status
        HttpStatus httpStatus = HttpStatus.OK;
        // 设置 Headers
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
        // 错误就发送 500 错误
        if (user == null) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(user, httpHeaders, httpStatus);
    }

}
```

## 五、启动类加上注解 @EnableSwagger2

在启动类上加上 @EnableSwagger2 注解以开启 Swagger。

```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
```

## 六、访问 Swagger API

项目创建完成后，本地启动然后输入地址 http://localhost:8080/v2/api-docs，可以看见 Swagger API 接口返回的 JSON 信息。

```json
{
    "swagger": "2.0",
    "info": {
        "description": "This is a swagger project.",
        "version": "1.0.0",
        "title": "swagger-example-service"
    },
    "host": "192.168.2.11:32256",
    "basePath": "/",
    "tags": [
        {
            "name": "Example Controller",
            "description": "Example Controller"
        },
        {
            "name": "basic-error-controller",
            "description": "Basic Error Controller"
        }
    ],
    "paths": {
        "/error": {
            "get": {
                "tags": [
                    "basic-error-controller"
                ]

......
```

## 七、将项目进行 Maven 编译

```bash
$ mvn clean install
```

## 八、执行 Dockerfile 编译

**Dockerfile**

```dockerfile
FROM openjdk:8u212-b04-jre-slim
VOLUME /tmp
ADD target/*.jar app.jar
RUN sh -c 'touch /app.jar'
ENV JAVA_OPTS="-Duser.timezone=Asia/Shanghai"
ENV APP_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar $APP_OPTS" ]
```

**执行 Docker 编译命令**

```bash
$ docker build -t mydlq/swagger-example-service:1.0.0
```

## 九、在 Kubernetes 集群启动该镜像

将上面编译的镜像启动到 Kubernetes 集群中，注意：本人已经将示例镜像推送到官方 Hub 仓库，可以按下yaml文件的镜像地址下载

- 镜像：mydlq/swagger-example-service:1.0.0

**swagger-example-service.yaml**

```yaml
apiVersion: v1
kind: Service
metadata:
  name: swagger-example-service
  labels:
    app: swagger-example-service
spec:
  ports:
  - name: tcp
    port: 8080
    nodePort: 32255
    targetPort: 8080
  type: NodePort
  selector:
    app: swagger-example-service
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: swagger-example-service
  labels:
    app: swagger-example-service
spec:
  selector:
    matchLabels:
      app: swagger-example-service
  template:
    metadata:
      labels:
        app: swagger-example-service
    spec:
      containers:
      - name: swagger-example-service
        image: mydlq/swagger-example-service:1.0.0
        ports:
        - containerPort: 8080
```

**kubernetes 集群启动该镜像**

> 请提前修改启动的 Namespace

```bash
$ kubectl apply -f swagger-example-service.yaml
```
