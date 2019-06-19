package club.mydlq.swagger.kubernetes.swagger;

import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Author: mydlq / 小豆丁
 * Blog:   http://www.mydlq.club
 * Github: https://github.com/my-dlq/
 *
 * Describe: Replace the CSS file for the Swagger Ui
 */
public class SwaggerUIMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/webjars/bycdao-ui/cdao/**").addResourceLocations("classpath:/css/");
    }

}