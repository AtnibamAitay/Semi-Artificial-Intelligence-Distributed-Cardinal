package space.atnibam.common.swagger.annotation;

import org.springframework.context.annotation.Import;
import space.atnibam.common.swagger.config.SwaggerAutoConfiguration;

import java.lang.annotation.*;

/**
 * @ClassName: EnableCustomSwagger
 * @Description: 自定义Swagger启用注解。
 * @Author: AtnibamAitay
 * @CreateTime: 2023-10-15 00:26
 **/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({SwaggerAutoConfiguration.class})
public @interface EnableCustomSwagger {
}
