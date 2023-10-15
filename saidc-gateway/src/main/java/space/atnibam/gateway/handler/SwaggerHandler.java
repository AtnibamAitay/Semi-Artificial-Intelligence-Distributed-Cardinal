package space.atnibam.gateway.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import springfox.documentation.swagger.web.*;

import java.util.Optional;

/**
 * @ClassName: SwaggerHandler
 * @Description: 为Swagger UI提供安全配置、UI配置以及资源信息
 * @Author: AtnibamAitay
 * @CreateTime: 2023-10-15 14:46
 **/
@RestController
@RequestMapping("/swagger-resources")
public class SwaggerHandler {
    private final SwaggerResourcesProvider swaggerResources;

    /**
     * Swagger的安全配置，如果没有配置，则使用默认设置
     */
    @Autowired(required = false)
    private SecurityConfiguration securityConfiguration;

    /**
     * Swagger的UI配置，如果没有配置，则使用默认设置
     */
    @Autowired(required = false)
    private UiConfiguration uiConfiguration;

    /**
     * 构造函数，注入Swagger资源信息提供对象
     *
     * @param swaggerResources Swagger资源信息提供对象
     */
    @Autowired
    public SwaggerHandler(SwaggerResourcesProvider swaggerResources) {
        this.swaggerResources = swaggerResources;
    }

    /**
     * 获取Swagger的安全配置信息
     *
     * @return 安全配置信息
     */
    @GetMapping("/configuration/security")
    public Mono<ResponseEntity<SecurityConfiguration>> securityConfiguration() {
        return Mono.just(new ResponseEntity<>(
                Optional.ofNullable(securityConfiguration).orElse(SecurityConfigurationBuilder.builder().build()),
                HttpStatus.OK));
    }

    /**
     * 获取Swagger的UI配置信息
     *
     * @return UI配置信息
     */
    @GetMapping("/configuration/ui")
    public Mono<ResponseEntity<UiConfiguration>> uiConfiguration() {
        return Mono.just(new ResponseEntity<>(
                Optional.ofNullable(uiConfiguration).orElse(UiConfigurationBuilder.builder().build()), HttpStatus.OK));
    }

    /**
     * 获取Swagger的资源信息
     *
     * @return 资源配置信息
     */
    @SuppressWarnings("rawtypes")
    @GetMapping("")
    public Mono<ResponseEntity> swaggerResources() {
        return Mono.just((new ResponseEntity<>(swaggerResources.get(), HttpStatus.OK)));
    }
}
