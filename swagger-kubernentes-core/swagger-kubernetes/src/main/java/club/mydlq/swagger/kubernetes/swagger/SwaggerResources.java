package club.mydlq.swagger.kubernetes.swagger;

import club.mydlq.swagger.kubernetes.param.SwaggerAutoConfig;
import club.mydlq.swagger.kubernetes.entity.ServiceInfo;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;
import java.util.ArrayList;
import java.util.List;

public class SwaggerResources implements SwaggerResourcesProvider {

    // serviceInfo list
    List<ServiceInfo> serviceInfos = new ArrayList<>();
    private SwaggerAutoConfig swaggerAutoConfig;

    public SwaggerResources(SwaggerAutoConfig swaggerAutoConfig) {
        this.swaggerAutoConfig = swaggerAutoConfig;
    }

    /**
     * 更新服务列表
     * Update service list
     *
     * @param serviceInfos
     */
    public void updateServiceInfos(List<ServiceInfo> serviceInfos) {
        this.serviceInfos = serviceInfos;
    }

    /**
     * 增加 SwaggerResource 对象到 swagger 列表
     * add SwaggerResource to swagger list
     *
     * @return
     */
    @Override
    public List<SwaggerResource> get() {
        List<SwaggerResource> resources = new ArrayList<>();
        for (ServiceInfo serviceInfo : serviceInfos) {
            resources.add(swaggerResource(serviceInfo.getName(), "/"
                    + serviceInfo.getName()
                    + swaggerAutoConfig.getDocApiPath(), swaggerAutoConfig.getSwaggerVersion()));
        }
        return resources;
    }


    /**
     * 创建 SwaggerResource 对象
     * crate SwaggerResource Object
     *
     * @param name
     * @param location
     * @param swaggerVersion
     * @return
     */
    private SwaggerResource swaggerResource(String name, String location, String swaggerVersion) {
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(name);
        swaggerResource.setLocation(location);
        swaggerResource.setSwaggerVersion(swaggerVersion);
        return swaggerResource;
    }

}
