package space.atnibam.common.swagger.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: SwaggerProperties
 * @Description: 用于存储与Swagger相关的属性
 * @Author: AtnibamAitay
 * @CreateTime: 2023-10-15 00:40
 **/
@Data
@Component
@ConfigurationProperties(prefix = "swagger")
public class SwaggerProperties {

    /**
     * 是否开启swagger，默认开启
     */
    private Boolean enabled = true;

    /**
     * 分组名
     */
    private String groupName = "default";

    /**
     * swagger会解析的包路径
     */
    private String basePackage = "";

    /**
     * swagger会解析的url规则
     */
    private List<String> basePath = new ArrayList<>();

    /**
     * 在basePath基础上需要排除的url规则
     */
    private List<String> excludePath = new ArrayList<>();

    /**
     * 标题
     */
    private String title = "Semi-Artificial-Intelligence-Distributed-Cardinal-API-Docs";

    /**
     * 描述
     */
    private String description = "SAIDC API文档";

    /**
     * 版本
     */
    private String version = "";

    /**
     * 许可证
     */
    private String license = "";

    /**
     * 许可证URL
     */
    private String licenseUrl = "";

    /**
     * 服务条款URL
     */
    private String termsOfServiceUrl = "";

    /**
     * host信息
     */
    private String host = "";

    /**
     * 联系人信息
     */
    private Contact contact = new Contact();

    /**
     * @ClassName: Contact
     * @Description: 联系人信息类，包含联系人姓名、URL和电子邮件
     * @Author: AtnibamAitay
     * @CreateTime: 2023-10-15 00:45
     */
    @Data
    @NoArgsConstructor
    public static class Contact {

        /**
         * 联系人
         */
        private String name = "AtnibamAitay";

        /**
         * 联系人url
         */
        private String url = "https://atnibam.space/";

        /**
         * 联系人email
         */
        private String email = "";
    }
}
