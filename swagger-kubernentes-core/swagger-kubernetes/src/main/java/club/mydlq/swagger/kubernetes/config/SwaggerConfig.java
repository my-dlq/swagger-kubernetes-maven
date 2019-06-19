package club.mydlq.swagger.kubernetes.config;

import club.mydlq.swagger.kubernetes.param.SwaggerAutoConfig;
import club.mydlq.swagger.kubernetes.swagger.SwaggerResources;
import club.mydlq.swagger.kubernetes.swagger.SwaggerUIMvcConfig;
import com.github.xiaoymin.swaggerbootstrapui.annotations.EnableSwaggerBootstrapUI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@EnableSwaggerBootstrapUI
@EnableConfigurationProperties(SwaggerAutoConfig.class)
public class SwaggerConfig {

    @Autowired
    private SwaggerAutoConfig swaggerAutoConfig;

    @Bean
    @Primary
    public SwaggerResources swaggerResourcesProcessor() {
        return new SwaggerResources(swaggerAutoConfig);
    }

    @Bean
    public SwaggerUIMvcConfig swaggerUIModifyMvcConfig() {
        return new SwaggerUIMvcConfig();
    }

}
