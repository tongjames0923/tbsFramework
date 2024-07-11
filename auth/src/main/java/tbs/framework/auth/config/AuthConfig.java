package tbs.framework.auth.config;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tbs.framework.auth.aspects.ControllerAspect;
import tbs.framework.auth.config.interceptors.TokenInterceptor;
import tbs.framework.auth.config.interceptors.UserModelInterceptor;
import tbs.framework.auth.interfaces.*;
import tbs.framework.auth.interfaces.impls.AnnotationPermissionValidator;
import tbs.framework.auth.interfaces.impls.CopyRuntimeDataExchanger;
import tbs.framework.auth.interfaces.impls.SimpleLogErrorHandler;
import tbs.framework.auth.model.RuntimeData;
import tbs.framework.auth.properties.AuthProperty;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

public class AuthConfig {

    @Resource
    private AuthProperty authProperty;

    @Bean
    @ConditionalOnMissingBean(IRequestTokenPicker.class)
    public IRequestTokenPicker requestTokenPicker()
        throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (null == authProperty.getTokenPicker()) {
            throw new IllegalStateException("No auth property 'token picker' has been configured");
        }
        if (StrUtil.isEmpty(this.authProperty.getTokenField())) {
            throw new IllegalStateException("No auth property 'token field' has been configured");
        }
        return this.authProperty.getTokenPicker().getConstructor().newInstance();
    }

    @Bean
    @RequestScope
    RuntimeData runtimeData() {
        return new RuntimeData();
    }

    @Bean
    public HttpMessageConverter<String> responseBodyConverter() {
        StringHttpMessageConverter converter = new StringHttpMessageConverter(Charset.forName("UTF-8"));
        return converter;
    }

    @Bean
    WebMvcConfigurer authWebMvcConfigurer(final IRequestTokenPicker requestTokenPicker,
        final IUserModelPicker userModelPicker, ObjectMapper objectMapper,
        HttpMessageConverter<String> responseBodyConverter) {
        return new WebMvcConfigurer() {

            //

            //2.2：解决No converter found for return value of type: xxxx
            public MappingJackson2HttpMessageConverter messageConverter() {
                MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
                converter.setObjectMapper(objectMapper);
                return converter;
            }

            @Override
            public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {

                //解决中文乱码
                converters.add(responseBodyConverter);
                //解决： 添加解决中文乱码后的配置之后，返回json数据直接报错 500：no convertter for return value of type
                //或这个：Could not find acceptable representation
                converters.add(messageConverter());
            }

            @Override
            public void addInterceptors(final InterceptorRegistry registry) {
                registry.addInterceptor(new TokenInterceptor(requestTokenPicker))
                    .addPathPatterns(AuthConfig.this.authProperty.getAuthPathPattern()).order(0);
                registry.addInterceptor(new UserModelInterceptor(userModelPicker))
                    .addPathPatterns(AuthConfig.this.authProperty.getAuthPathPattern()).order(1);
                WebMvcConfigurer.super.addInterceptors(registry);
            }
        };
    }

    @Bean
    @ConditionalOnMissingBean(IRuntimeDataExchanger.class)
    IRuntimeDataExchanger runtimeDataExchanger() {
        return new CopyRuntimeDataExchanger();
    }

    @Bean
    public ControllerAspect controllerAspect(final Map<String, IPermissionValidator> map) {
        return new ControllerAspect(map);
    }

    @Bean
    @ConditionalOnMissingBean(IErrorHandler.class)
    IErrorHandler errorHandler() {
        return new SimpleLogErrorHandler();
    }

    @Bean
    @ConditionalOnProperty(name = "tbs.framework.auth.enable-annotation-permission-validator", havingValue = "true")
    IPermissionValidator permissionValidator() {
        return new AnnotationPermissionValidator();
    }

    @Bean
    @ConditionalOnProperty(name = "tbs.framework.auth.enable-cors", havingValue = "true")
    public CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration corsConfiguration = new CorsConfiguration();
        // 1 设置访问源地址
        corsConfiguration.addAllowedOrigin("*");
        // 2 设置访问源请求头
        corsConfiguration.addAllowedHeader("*");
        // 3 设置访问源请求方法
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.setMaxAge(24 * 60 * 60L);
        // 4 对接口配置跨域设置
        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(source);
    }

}
