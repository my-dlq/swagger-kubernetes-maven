package club.mydlq.swagger.kubernetes.param;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("swagger.discovery")
public class DiscoveryAutoConfig {

    // Discovery interval
    private long interval = 30;

    // Discovery initial delay
    private long initialDelay = 30;

    // Discovery kubernetes url
    private String url = "";

    // Discovery kubernetes namespace
    private String namespace = "";

    // Discovery kubernetes port type
    private String portType = "ClusterIP";
}
