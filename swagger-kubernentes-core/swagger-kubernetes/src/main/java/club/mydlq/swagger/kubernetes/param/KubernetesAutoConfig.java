package club.mydlq.swagger.kubernetes.param;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("swagger.kubernetes.connect")
public class KubernetesAutoConfig {

    // Kubernetes connection from cluster
    private boolean fromCluster = true;

    // Kubernetes url
    private String url = "";

    // Kubernetes authentication token
    private String token = "";

    // Kubernetes token file path
    private String tokenPath = "";

    // Validate SSL certificate
    boolean validateSsl = false;

    // Kubernetes ssl ca file path
    private String caPath = "";

    // Kubernetes config file path
    private String fromConfigPath = "";

    // Whether to find a file from the default configuration address ($HOME/.kube/config)
    private boolean fromDefault = true;

}
