package space.atnibam.common.swagger.constant;

import java.util.Arrays;
import java.util.List;

/**
 * @ClassName: SwaggerConstants
 * @Description: 定义Swagger相关的常量
 * @Author: AtnibamAitay
 * @CreateTime: 2023-10-15 00:50
 **/
public class SwaggerConstants {

    /**
     * 默认校验KEY
     */
    public static final String ACCESS_TOKEN_KEY = "Authorization";

    /**
     * 默认的排除路径，排除Spring Boot默认的错误处理路径和端点
     */
    public static final List<String> DEFAULT_EXCLUDE_PATH = Arrays.asList("/error", "/actuator/**");

    /**
     * 默认根路径
     */
    public static final String DEFAULT_BASE_PATH = "/**";

    /**
     * 默认包
     */
    public static final String BASE_PACKAGE = "space.atnibam";

    /**
     * 表示全局作用域的字符串常量，用于构建 AuthorizationScope 对象。
     */
    public static final String GLOBAL_SCOPE = "global";

    /**
     * 描述作用域权限的字符串常量，用于构建 AuthorizationScope 对象。
     */
    public static final String SCOPE_DESCRIPTION = "accessEverything";

    /**
     * 正则表达式字符串常量，用于在 SecurityContext 中匹配非 auth 开头的路径。
     */
    public static final String PATH_REGEX = "^(?!auth).*$";

    /**
     * 字符串常量，表示在 Swagger 安全模式配置中 token 的传递方式。
     */
    public static final String HEADER_TYPE = "header";

    /**
     * Swagger UI的访问路径
     */
    public static final String SWAGGER_UI_PATH = "/swagger-ui/**";

    /**
     * Swagger UI资源的位置
     */
    public static final String SWAGGER_UI_RESOURCE_LOCATION = "classpath:/META-INF/resources/webjars/springfox-swagger-ui/";

}
