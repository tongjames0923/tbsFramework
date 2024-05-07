package tbs.framework.swagger.config;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.Contact;
import springfox.documentation.spring.web.plugins.Docket;
import tbs.framework.base.log.ILogger;
import tbs.framework.base.utils.LogUtil;
import tbs.framework.swagger.properties.SwaggerProperty;

import javax.annotation.Resource;

/**
 * @author abstergo
 */
public class SwaggerConfig {

    @Resource
    private SwaggerProperty swaggerProperty;

    ILogger logger;

    public SwaggerConfig(LogUtil logUtil) {
        logger = logUtil.getLogger(this.getClass().getName());
    }

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

    @Bean
    @ConditionalOnProperty(name = "tbs.framework.swagger.show-document-url", havingValue = "true",
        matchIfMissing = true)
    ApplicationRunner showDocumentUrl() {
        String context = SpringUtil.getApplicationContext().getEnvironment().getProperty("server.servlet.context-path");
        context = StrUtil.isEmpty(context) ? "" : context;

        String finalContext = context;
        return new ApplicationRunner() {
            @Override
            public void run(ApplicationArguments args) throws Exception {
                SwaggerConfig.this.logger.trace(String.format("visit: http://127.0.0.1:%s%s/doc.html",
                    SpringUtil.getApplicationContext().getEnvironment().getProperty("server.port"), finalContext));
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
