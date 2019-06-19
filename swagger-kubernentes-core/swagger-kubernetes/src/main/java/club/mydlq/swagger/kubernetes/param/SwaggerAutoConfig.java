package club.mydlq.swagger.kubernetes.param;

import lombok.Data;
import java.util.Set;
import java.util.HashSet;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("swagger.global")
public class SwaggerAutoConfig {

    // Global swagger docs api path
    private String docApiPath = "/v2/api-docs";
    // Global Swagger docs version
    private String swaggerVersion = "2.0";

    // Ignore service list
    private Set<String> ignoreServices = new HashSet<>();
}
