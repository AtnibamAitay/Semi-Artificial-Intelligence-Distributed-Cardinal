package space.atnibam.common.swagger.config;

import io.swagger.annotations.Api;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import space.atnibam.common.swagger.constant.SwaggerConstants;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.ApiSelectorBuilder;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import static space.atnibam.common.swagger.constant.SwaggerConstants.*;

/**
 * @ClassName: SwaggerAutoConfiguration
 * @Description: Swagger 自动配置类，用于生成 API 文档并支持 OAuth2 鉴权
 * @Author: AtnibamAitay
 * @CreateTime: 2023-10-15 12:12
 **/
@EnableOpenApi
@Configuration
@ConditionalOnProperty(name = "swagger.enabled", matchIfMissing = true)
public class SwaggerAutoConfiguration {

    /**
     * 创建 SwaggerProperties Bean 的方法，如果 Spring 容器中没有该 Bean，则创建一个新的。
     *
     * @return SwaggerProperties 对象实例
     */
    @Bean
    @ConditionalOnMissingBean
    public SwaggerProperties swaggerProperties() {
        return new SwaggerProperties();
    }

    /**
     * 创建 Docket Bean 的方法，Docket 是 Swagger 的核心配置，通过这个配置来生成对应的 API 文档。
     *
     * @param swaggerProperties SwaggerProperties对象实例，包含了Swagger文档所需的各种配置
     * @return Docket 对象实例
     */
    @Bean
    public Docket api(SwaggerProperties swaggerProperties) {
        // 如果 base-package 为空，则设置为默认值
        if (swaggerProperties.getBasePackage().isEmpty()) {
            swaggerProperties.setBasePackage(SwaggerConstants.BASE_PACKAGE);
        }

        // 如果 base-path 为空，则设置为默认值
        if (swaggerProperties.getBasePath().isEmpty()) {
            swaggerProperties.getBasePath().add(SwaggerConstants.DEFAULT_BASE_PATH);
        }
        List<Predicate<String>> basePath = new ArrayList<>();
        // 将所有 base-path 转换为 Predicate 对象后添加到列表中
        swaggerProperties.getBasePath().forEach(path -> basePath.add(PathSelectors.ant(path)));

        // 如果 exclude-path 为空，则设置为默认值
        if (swaggerProperties.getExcludePath().isEmpty()) {
            swaggerProperties.getExcludePath().addAll(SwaggerConstants.DEFAULT_EXCLUDE_PATH);
        }
        List<Predicate<String>> excludePath = new ArrayList<>();
        // 将所有 exclude-path 转换为 Predicate 对象后添加到列表中
        swaggerProperties.getExcludePath().forEach(path -> excludePath.add(PathSelectors.ant(path)));

        // 构建 Docket 对象
        ApiSelectorBuilder builder = new Docket(DocumentationType.SWAGGER_2)
                .enable(swaggerProperties.getEnabled())
                .host(swaggerProperties.getHost())
                .apiInfo(apiInfo(swaggerProperties))
                .select()
                .apis(RequestHandlerSelectors.basePackage(swaggerProperties.getBasePackage()))
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class));

        // 添加所有的 base-path 到 builder 中
        swaggerProperties.getBasePath().forEach(p -> builder.paths(PathSelectors.ant(p)));
        // 添加所有的 exclude-path 到 builder 中，并使用 negate() 方法将其设置为非匹配
        swaggerProperties.getExcludePath().forEach(p -> builder.paths(PathSelectors.ant(p).negate()));

        // 构建 Docket 对象并返回
        return builder
                .build()
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts())
                .pathMapping("/");
    }

    /**
     * 安全模式的配置，指定了 token 通过 Authorization 请求头传递。
     *
     * @return SecurityScheme 对象列表
     */
    private List<SecurityScheme> securitySchemes() {
        SecurityScheme scheme = new ApiKey(SwaggerConstants.ACCESS_TOKEN_KEY, SwaggerConstants.ACCESS_TOKEN_KEY, HEADER_TYPE);
        return Collections.singletonList(scheme);
    }

    /**
     * 配置全局鉴权策略的开关，通过正则表达式进行匹配，此处默认匹配所有 URL。
     *
     * @return SecurityContext 对象列表
     */
    private List<SecurityContext> securityContexts() {
        SecurityContext context = SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(PathSelectors.regex(PATH_REGEX))
                .build();
        return Collections.singletonList(context);
    }

    /**
     * 默认的全局鉴权策略配置方法。
     *
     * @return SecurityReference 对象列表
     */
    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope(GLOBAL_SCOPE, SCOPE_DESCRIPTION);
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Collections.singletonList(
                new SecurityReference(SwaggerConstants.ACCESS_TOKEN_KEY, authorizationScopes));
    }

    /**
     * api文档的详细信息函数。
     *
     * @param swaggerProperties SwaggerProperties对象实例，包含了Swagger文档所需的各种配置
     * @return API 信息对象
     */
    private ApiInfo apiInfo(SwaggerProperties swaggerProperties) {
        return new ApiInfoBuilder()
                .title(swaggerProperties.getTitle())
                .description(swaggerProperties.getDescription())
                .license(swaggerProperties.getLicense())
                .licenseUrl(swaggerProperties.getLicenseUrl())
                .termsOfServiceUrl(swaggerProperties.getTermsOfServiceUrl())
                .contact(new Contact(swaggerProperties.getContact().getName(), swaggerProperties.getContact().getUrl(), swaggerProperties.getContact().getEmail()))
                .version(swaggerProperties.getVersion())
                .build();
    }
}
