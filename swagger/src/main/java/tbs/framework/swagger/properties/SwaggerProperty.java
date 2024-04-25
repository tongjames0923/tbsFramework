package tbs.framework.swagger.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import springfox.documentation.spi.DocumentationType;

@Data
@ConfigurationProperties("tbs.framework.swagger")
public class SwaggerProperty {
    private String title = "demo";
    private String description = "description";
    private String version = "1.0.1";
    private String termsOfService="termsOfService";
    private String license="license";
    private String licenseUrl="licenseUrl";
    private String contact="contact";
    private String contactUrl="contactUrl";
    private String basePackage;
    private String groupName="group";
    private String email="email";
    private String pathPattern = "";
    private DocumentationType documentationType = DocumentationType.OAS_30;
}
