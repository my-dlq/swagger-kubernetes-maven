package club.mydlq.swagger.kubernetes.config;

import lombok.extern.slf4j.Slf4j;
import club.mydlq.swagger.kubernetes.discovery.KubernetesConnect;
import club.mydlq.swagger.kubernetes.discovery.KubernetesDiscovery;
import club.mydlq.swagger.kubernetes.param.DiscoveryAutoConfig;
import club.mydlq.swagger.kubernetes.param.KubernetesAutoConfig;
import club.mydlq.swagger.kubernetes.param.SwaggerAutoConfig;
import club.mydlq.swagger.kubernetes.swagger.SwaggerResources;
import club.mydlq.swagger.kubernetes.zuul.RefreshRoute;
import club.mydlq.swagger.kubernetes.zuul.ZuulRouteLocator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@EnableScheduling
@EnableConfigurationProperties({KubernetesAutoConfig.class, DiscoveryAutoConfig.class})
public class DiscoveryConfig {

    @Autowired
    SwaggerResources swaggerResourcesProcessor;

    @Autowired
    ZuulRouteLocator zuulRouteLocator;

    @Autowired
    RefreshRoute refreshRouteService;

    @Autowired
    SwaggerAutoConfig swaggerAutoConfig;

    @Autowired
    protected ZuulProperties zuulProperties;

    @Autowired
    private DiscoveryAutoConfig discoveryAutoConfig;

    @Autowired
    KubernetesAutoConfig kubernetesAutoConfig;

    @Bean
    public KubernetesConnect connectKubernetes() {
        return new KubernetesConnect(kubernetesAutoConfig);
    }

    @Bean
    public KubernetesDiscovery kubernetesDiscovery() {
        return new KubernetesDiscovery(swaggerResourcesProcessor, zuulRouteLocator,
                refreshRouteService, swaggerAutoConfig, zuulProperties, discoveryAutoConfig);
    }

    @Bean
    public StartConnection startConnection() {
        return new StartConnection();
    }

}
