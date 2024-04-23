package tbs.framework.auth.config;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tbs.framework.auth.config.interceptors.TokenInterceptor;
import tbs.framework.auth.config.interceptors.UserModelInterceptor;
import tbs.framework.auth.interfaces.IRequestTokenPicker;
import tbs.framework.auth.interfaces.IUserModelPicker;
import tbs.framework.auth.model.RuntimeData;
import tbs.framework.auth.properties.AuthProperty;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;

public class AuthConfig implements WebMvcConfigurer {

    @Resource
    private AuthProperty authProperty;

    @Bean
    @ConditionalOnMissingBean(IRequestTokenPicker.class)
    public IRequestTokenPicker requestTokenPicker()
        throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (authProperty.getTokenPicker() == null) {
            throw new IllegalStateException("No auth property 'token picker' has been configured");
        }
        if (StrUtil.isEmpty(authProperty.getTokenField())) {
            throw new IllegalStateException("No auth property 'token field' has been configured");
        }
        return authProperty.getTokenPicker().getConstructor().newInstance();
    }

    @Bean
    @RequestScope
    RuntimeData runtimeData() {
        return new RuntimeData();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new TokenInterceptor(SpringUtil.getBean(IRequestTokenPicker.class)))
            .addPathPatterns(authProperty.getTokenPickUrlPatterns()).order(0);
        registry.addInterceptor(new UserModelInterceptor(SpringUtil.getBean(IUserModelPicker.class)))
            .addPathPatterns(authProperty.getTokenPickUrlPatterns()).order(1);
        WebMvcConfigurer.super.addInterceptors(registry);
    }

    @Bean
    @ConditionalOnProperty(name = "tbs.framework.auth.enableCors", havingValue = "false")
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
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
