package club.mydlq.swagger.kubernetes;

import club.mydlq.swagger.kubernetes.config.*;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ComponentScan
@Import({DiscoveryConfig.class, SwaggerConfig.class, ZuulConfig.class})
public @interface EnableSwaggerKubernetes {
}
