package club.mydlq.swagger.kubernetes.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ServiceInfo {
    private String name;
    private String host;
    private Integer port;
    private String path;
}

