package club.mydlq.swagger.kubernetes.discovery;

import club.mydlq.swagger.kubernetes.param.DiscoveryAutoConfig;
import club.mydlq.swagger.kubernetes.param.SwaggerAutoConfig;
import club.mydlq.swagger.kubernetes.entity.ServiceInfo;
import club.mydlq.swagger.kubernetes.swagger.SwaggerResources;
import club.mydlq.swagger.kubernetes.utils.FileUtils;
import club.mydlq.swagger.kubernetes.utils.HttpUtils;
import club.mydlq.swagger.kubernetes.utils.ValidationUtils;
import club.mydlq.swagger.kubernetes.zuul.RefreshRoute;
import club.mydlq.swagger.kubernetes.zuul.ZuulRouteLocator;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.*;
import io.kubernetes.client.util.Config;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties.ZuulRoute;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.IntervalTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Author: mydlq / 小豆丁
 * Blog:   http://www.mydlq.club
 * Github: https://github.com/my-dlq/
 * <p>
 * Describe: kubernetes Service Discovery
 */
@Slf4j
public class KubernetesDiscovery implements SchedulingConfigurer {

    static final String PORT_TYPE_NODEPORT = "NodePort";

    private DiscoveryAutoConfig discoveryAutoConfig;
    private SwaggerResources swaggerResourcesProcessor;
    private ZuulRouteLocator zuulRouteLocator;
    private RefreshRoute refreshRouteService;
    private SwaggerAutoConfig swaggerAutoConfig;
    private ZuulProperties zuulProperties;

    public KubernetesDiscovery(SwaggerResources swaggerResourcesProcessor,
                               ZuulRouteLocator zuulRouteLocator,
                               RefreshRoute refreshRouteService,
                               SwaggerAutoConfig swaggerAutoConfig,
                               ZuulProperties zuulProperties,
                               DiscoveryAutoConfig discoveryAutoConfig) {
        this.swaggerResourcesProcessor = swaggerResourcesProcessor;
        this.zuulRouteLocator = zuulRouteLocator;
        this.refreshRouteService = refreshRouteService;
        this.swaggerAutoConfig = swaggerAutoConfig;
        this.zuulProperties = zuulProperties;
        this.discoveryAutoConfig = discoveryAutoConfig;
    }

    /**
     * 刷新服务列表
     * Refresh service list
     */
    public void freshServiceList() {
        serviceFresh();
    }

    /**
     * 定时任务
     * timed task
     */
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addFixedRateTask(
                new IntervalTask(
                        this::serviceFresh,
                        discoveryAutoConfig.getInterval() * 1000,
                        discoveryAutoConfig.getInitialDelay() * 1000));
    }

    /**
     * 刷新服务列表
     * fresh kubernetes service list
     */
    private void serviceFresh() {
        // 验证 Namespace 是否设置，如果未设置则默认读取集群所在的 Namespace
        readNamespace();
        // 获取动态服务列表
        List<ServiceInfo> serviceInfos = getServiceInfo(discoveryAutoConfig.getNamespace(),
                discoveryAutoConfig.getPortType(),
                discoveryAutoConfig.getUrl(),
                swaggerAutoConfig.getDocApiPath());
        // 获取静态服务列表
        List<ServiceInfo> staticServiceList = getStaticServiceList();
        // 将静态服务列表加入总服务列表
        serviceInfos.addAll(staticServiceList);
        // 忽略用户指定的服务
        excludeService(serviceInfos, swaggerAutoConfig.getIgnoreServices());
        // 刷新 swagger 服务列表
        swaggerResourcesProcessor.updateServiceInfos(serviceInfos);
        // 刷新 zuul 服务列表
        zuulRouteLocator.setServiceInfos(serviceInfos);
        refreshRouteService.refreshRoute();
    }

    /**
     * 获取静态服务列表
     * get static service list from zuul properties
     */
    private List<ServiceInfo> getStaticServiceList() {
        List<ServiceInfo> serviceInfoList = new ArrayList<>();
        for (ZuulRoute route : zuulProperties.getRoutes().values()) {
            ServiceInfo serviceInfo = analysisStaticService(route);
            if (serviceInfo != null) {
                serviceInfoList.add(serviceInfo);
            }
        }
        return serviceInfoList;
    }

    /**
     * 分析静态服务
     * @param route
     * @return
     */
    private ServiceInfo analysisStaticService(ZuulRoute route) {
        boolean isVerify = true;
        ServiceInfo serviceInfo = new ServiceInfo();
        // 非空验证
        if (route.getUrl() == null || route.getPath() == null) {
            isVerify = false;
        }
        // 如果不是以 http 或者 https 开始,则默认加上 "http://"
        if (!(route.getUrl().startsWith("http") || route.getUrl().startsWith("https"))) {
            route.setUrl("http://" + route.getUrl());
        }
        // 如果为域名,则默认加上 "80" 端口
        if (!ValidationUtils.validatePort(route.getUrl())) {
            route.setUrl(route.getUrl() + ":80");
        }
        // URL验证
        if (!ValidationUtils.validateUrl(route.getUrl())) {
            isVerify = false;
        }
        // 截取 URL,设置Host & Port
        String host = StringUtils.substringBeforeLast(route.getUrl(), ":");
        String port = StringUtils.substringAfterLast(route.getUrl(), ":");
        // 检测截取端口是否为数字
        if (!StringUtils.isNumeric(port)) {
            isVerify = false;
        }
        if (!isVerify) {
            return null;
        }
        serviceInfo.setHost(host);
        serviceInfo.setPort(Integer.parseInt(port));
        // Path验证
        if (!route.getPath().startsWith("/")) {
            route.setPath("/" + route.getPath());
        }
        // 拆分Path,设置Name
        String[] paths = route.getPath().split("/");
        serviceInfo.setName(paths[1]);
        // 设置SwaggerUrl
        serviceInfo.setPath(discoveryAutoConfig.getUrl());
        return serviceInfo;
    }

    /**
     * 排除设置的不需要发现的服务
     * exclude specified services
     */
    private void excludeService(List<ServiceInfo> serviceInfoList, Set<String> excludeServiceList) {
        if (excludeServiceList == null) {
            return;
        }
        List<ServiceInfo> excludeServiceInfoList = new ArrayList<>();
        for (String serviceName : excludeServiceList) {
            for (ServiceInfo serviceInfo : serviceInfoList) {
                if (StringUtils.equalsIgnoreCase(serviceInfo.getName(), serviceName)) {
                    excludeServiceInfoList.add(serviceInfo);
                }
            }
        }
        serviceInfoList.removeAll(excludeServiceInfoList);
    }

    /**
     * 获取 ServiceInfo 列表
     *
     * @param namespace
     * @param host
     * @param portType   ClusterIP Or NodePort
     * @param swaggerUrl
     * @return
     */
    private static List<ServiceInfo> getServiceInfo(String namespace, String portType, String host, String swaggerUrl) {
        if (StringUtils.isEmpty(namespace)) {
            throw new NullPointerException("namespace is null!");
        }
        List<ServiceInfo> serviceInfos = new ArrayList<>();
        // 从 Kubernetes 集群获取 Service 列表
        List<V1Service> serviceList = getKubernetesServiceList(namespace, portType);
        // 检测 swagger url 是否以 "/" 开始
        if (!swaggerUrl.startsWith("/")) {
            swaggerUrl = "/" + swaggerUrl;
        }
        // 检测接口是否符合要求
        for (V1Service service : serviceList) {
            // 设置 host
            String serviceHost = "http://" + service.getMetadata().getName() + "." + service.getMetadata().getNamespace();
            if (portType.equalsIgnoreCase(PORT_TYPE_NODEPORT)) {
                serviceHost = host;
            }
            // 获取端口列表
            Integer[] ports = getPort(service, portType);
            if (ports == null) {
                continue;
            }
            // 根据 Port & swaggerUrl 检查地址是否是 Swagger Api 来确定是否加入服务列表
            for (Integer port : ports) {
                log.debug(serviceHost + ":" + port + swaggerUrl);
                ServiceInfo serviceInfo = new ServiceInfo();
                serviceInfo.setName(service.getMetadata().getName());
                serviceInfo.setHost(serviceHost);
                serviceInfo.setPort(port);
                serviceInfo.setPath(swaggerUrl);
                serviceInfos.add(serviceInfo);
            }
            // 过滤 Service,只保留拥有swagger api的服务
            HttpUtils.checkUrl(serviceInfos);
        }
        return serviceInfos;
    }


    /**
     * 获得端口列表
     * get ports
     *
     * @param service kubernetes service info
     * @param type    ClusterIP or NodePort
     * @return
     */
    private static Integer[] getPort(V1Service service, String type) {
        List<Integer> ports = new ArrayList<>();
        if (service != null) {
            // 获取 Port 列表
            List<V1ServicePort> portList = service.getSpec().getPorts();
            for (V1ServicePort port : portList) {
                if (StringUtils.equalsIgnoreCase(type, PORT_TYPE_NODEPORT)) {
                    ports.add(port.getNodePort());
                } else {
                    ports.add(port.getPort());
                }
            }
        }
        return ports.toArray(new Integer[ports.size()]);
    }

    /**
     * 从 Kubernetes 中获取 Service 列表
     *
     * @param namespace
     * @param portType
     * @return
     */
    private static List<V1Service> getKubernetesServiceList(String namespace, String portType) {
        // 设置 Api 客户端
        CoreV1Api api = new CoreV1Api();
        List<V1Service> kubernetesServiceList = new ArrayList<>();
        V1ServiceList serviceList = null;
        V1EndpointsList endPointList = null;
        // 获取 ServiceList & EndPointList
        try {
            serviceList = api.listNamespacedService(namespace, null, null, null, null, null, null, null, null, null);
            endPointList = api.listNamespacedEndpoints(namespace, null, null, null, null, null, null, null, null, null);
        } catch (ApiException e) {
            log.error(e.getMessage());
        }
        // 检测 Service 是否包含 Endpoints，如果端口类型为 NodePort,则检测端口类型
        if (endPointList != null && serviceList != null) {
            for (V1Service service : serviceList.getItems()) {
                if (!isContainEndpoints(endPointList, service.getMetadata().getName())
                        || StringUtils.equalsIgnoreCase(portType, PORT_TYPE_NODEPORT)
                        && !StringUtils.equalsIgnoreCase(service.getSpec().getType(), portType)) {
                    continue;
                }
                kubernetesServiceList.add(service);
            }
        }
        return kubernetesServiceList;
    }


    /**
     * 检测 Service 中是否包含 Endpoints
     *
     * @param endpointList
     * @param serviceName
     * @return
     */
    private static boolean isContainEndpoints(V1EndpointsList endpointList, String serviceName) {
        if (endpointList.getItems() != null) {
            for (V1Endpoints endpoints : endpointList.getItems()) {
                if (StringUtils.equalsIgnoreCase(endpoints.getMetadata().getName(), serviceName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 读取容器所在的 Namespace
     * Read the Namespace where the container is located
     */
    private void readNamespace() {
        if (StringUtils.isEmpty(discoveryAutoConfig.getNamespace())) {
            String namespace = FileUtils.readFile(Config.SERVICEACCOUNT_ROOT + "/namespace");
            discoveryAutoConfig.setNamespace(namespace);
            log.info("read namespace " + namespace);
        }
    }

}