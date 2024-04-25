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

public class SwaggerConfig {

    @Resource
    private SwaggerProperty swaggerProperty;

    @Bean
    WebMvcConfigurer swaggerConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
                registry.addResourceHandler("/webjars/**")
                    .addResourceLocations("classpath:/META-INF/resources/webjars/");
                WebMvcConfigurer.super.addResourceHandlers(registry);
            }
        };
    }

    @Bean(value = "defaultApi2")
    Docket defaultApi2() {
        return new Docket(swaggerProperty.getDocumentationType()).apiInfo(
                new ApiInfoBuilder().title(swaggerProperty.getTitle())
                    .termsOfServiceUrl(swaggerProperty.getTermsOfService()).version(swaggerProperty.getVersion()).contact(
                        new Contact(swaggerProperty.getContact(), swaggerProperty.getContactUrl(), swaggerProperty.getEmail()))
                    .description(swaggerProperty.getDescription()).license(swaggerProperty.getLicense())
                    .licenseUrl(swaggerProperty.getLicenseUrl()).build()).groupName(swaggerProperty.getGroupName()).select()
            .apis(RequestHandlerSelectors.basePackage(swaggerProperty.getBasePackage())).paths(
                StrUtil.isEmpty(swaggerProperty.getPathPattern()) ? PathSelectors.any()
                    : PathSelectors.regex(swaggerProperty.getPathPattern())).build();
    }

    //    @Bean("CustomRequestMappingHandlerMapping")
    //    RequestMappingHandlerMapping requestMappingHandlerMapping1() {
    //        return new RequestMappingHandlerMapping();
    //    }

}
