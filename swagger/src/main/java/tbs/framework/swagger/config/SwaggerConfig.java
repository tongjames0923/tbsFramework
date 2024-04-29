package tbs.framework.swagger.config;

import cn.hutool.core.util.StrUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.Contact;
import springfox.documentation.spring.web.plugins.Docket;
import tbs.framework.swagger.properties.SwaggerProperty;

import javax.annotation.Resource;

/**
 * @author abstergo
 */
public class SwaggerConfig {

    @Resource
    private SwaggerProperty swaggerProperty;

    @Bean
    WebMvcConfigurer swaggerConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addResourceHandlers(final ResourceHandlerRegistry registry) {
                registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
                registry.addResourceHandler("/webjars/**")
                    .addResourceLocations("classpath:/META-INF/resources/webjars/");
                WebMvcConfigurer.super.addResourceHandlers(registry);
            }
        };
    }

    @Bean("defaultApi2")
    Docket defaultApi2() {
        return new Docket(this.swaggerProperty.getDocumentationType()).apiInfo(
                new ApiInfoBuilder().title(this.swaggerProperty.getTitle())
                    .termsOfServiceUrl(this.swaggerProperty.getTermsOfService()).version(this.swaggerProperty.getVersion())
                    .contact(new Contact(this.swaggerProperty.getContact(), this.swaggerProperty.getContactUrl(),
                        this.swaggerProperty.getEmail())).description(this.swaggerProperty.getDescription())
                    .license(this.swaggerProperty.getLicense()).licenseUrl(this.swaggerProperty.getLicenseUrl()).build())
            .groupName(this.swaggerProperty.getGroupName()).select()
            .apis(RequestHandlerSelectors.basePackage(this.swaggerProperty.getBasePackage())).paths(
                StrUtil.isEmpty(this.swaggerProperty.getPathPattern()) ? PathSelectors.any()
                    : PathSelectors.regex(this.swaggerProperty.getPathPattern())).build();
    }
}
