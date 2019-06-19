package club.mydlq.swagger.kubernetes.config;

import club.mydlq.swagger.kubernetes.zuul.RefreshRoute;
import club.mydlq.swagger.kubernetes.zuul.ZuulRouteLocator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.context.annotation.Bean;

@EnableZuulProxy
public class ZuulConfig {

    @Autowired
    protected ZuulProperties zuulProperties;

    @Autowired
    protected ServerProperties server;

    @Bean
    public RefreshRoute refreshRoute() {
        return new RefreshRoute();
    }

    @Bean
    public ZuulRouteLocator routeLocator() {
        return new ZuulRouteLocator(server.getServlet().getContextPath(), zuulProperties);
    }

}
