package space.atnibam.common.swagger.config;

import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static space.atnibam.common.swagger.constant.SwaggerConstants.SWAGGER_UI_PATH;
import static space.atnibam.common.swagger.constant.SwaggerConstants.SWAGGER_UI_RESOURCE_LOCATION;

/**
 * @ClassName: SwaggerWebConfiguration
 * @Description: 配置Swagger的资源映射路径
 * @Author: AtnibamAitay
 * @CreateTime: 2023-10-15 00:46
 **/
public class SwaggerWebConfiguration implements WebMvcConfigurer {

    /**
     * 添加静态资源处理器，用于访问Swagger UI页面
     *
     * @param registry Spring MVC的资源处理器注册对象
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // swagger-ui 地址
        registry.addResourceHandler(SWAGGER_UI_PATH)
                .addResourceLocations(SWAGGER_UI_RESOURCE_LOCATION);
    }
}
