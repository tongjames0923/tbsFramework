package tbs.framework.swagger.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import springfox.documentation.spi.DocumentationType;

@Data
@ConfigurationProperties("tbs.framework.swagger")
public class SwaggerProperty {
    /**
     * 标题
     */
    private String title = "demo";
    /**
     * 描述
     */
    private String description = "description";
    /**
     * 版本
     */
    private String version = "1.0.1";
    /**
     * 周期服务
     */
    private String termsOfService="termsOfService";
    /**
     * 许可证
     */
    private String license="license";
    /**
     * 许可证url
     */
    private String licenseUrl="licenseUrl";
    /**
     *联系人
     */
    private String contact="contact";
    /**
     * 联系人url
     */
    private String contactUrl="contactUrl";
    /**
     * 扫描包路径
     */
    private String basePackage;
    /**
     * 分组
     */
    private String groupName="group";
    /**
     * 联系电子邮件
     */
    private String email="email";
    /**
     * 路径匹配
     */
    private String pathPattern = "";
    /**
     * 文档类型
     */
    private DocumentationType documentationType = DocumentationType.OAS_30;
}
