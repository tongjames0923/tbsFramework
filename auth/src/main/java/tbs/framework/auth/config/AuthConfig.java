package tbs.framework.auth.config;

import cn.hutool.core.collection.CollUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tbs.framework.auth.aspects.ControllerAspect;
import tbs.framework.auth.config.interceptors.RuntimeDataInterceptor;
import tbs.framework.auth.config.interceptors.TokenInterceptor;
import tbs.framework.auth.interfaces.IApiInterceptor;
import tbs.framework.auth.interfaces.IErrorHandler;
import tbs.framework.auth.interfaces.IRuntimeDataExchanger;
import tbs.framework.auth.interfaces.impls.CopyRuntimeDataExchanger;
import tbs.framework.auth.interfaces.impls.SimpleLogErrorHandler;
import tbs.framework.auth.interfaces.permission.IPermissionValidator;
import tbs.framework.auth.interfaces.permission.impls.AnnotationPermissionValidator;
import tbs.framework.auth.interfaces.permission.impls.ApiPermissionInterceptor;
import tbs.framework.auth.interfaces.token.IRequestTokenPicker;
import tbs.framework.auth.interfaces.token.ITokenParser;
import tbs.framework.auth.interfaces.token.IUserModelPicker;
import tbs.framework.auth.interfaces.token.impls.parser.UserModelTokenParser;
import tbs.framework.auth.model.RuntimeData;
import tbs.framework.auth.properties.AuthProperty;
import tbs.framework.base.utils.LogFactory;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        if (CollUtil.isEmpty(this.authProperty.getTokenFields())) {
            throw new IllegalStateException("No auth property 'token fields' has been configured");
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
    WebMvcConfigurer authWebMvcConfigurer(Map<String, IRequestTokenPicker> requestTokenPickers,
        ObjectMapper objectMapper, HttpMessageConverter<String> responseBodyConverter,
        Map<String, ITokenParser> tokenParsers) {
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
                registry.addInterceptor(new RuntimeDataInterceptor()).addPathPatterns("/**")
                    .order(Ordered.HIGHEST_PRECEDENCE);
                List<ITokenParser> parsers = tokenParsers.values().stream().collect(Collectors.toList());
                for (IRequestTokenPicker picker : requestTokenPickers.values()) {
                    if (CollUtil.isEmpty(picker.paths())) {
                        continue;
                    }
                    registry.addInterceptor(new TokenInterceptor(picker, parsers,
                            LogFactory.getInstance().getLogger(TokenInterceptor.class.getName())))
                        .addPathPatterns(picker.paths()).order(picker.getOrder());
                }
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
    @ConditionalOnProperty(name = "tbs.framework.auth.enable-annotation-permission-validator", havingValue = "true")
    ApiPermissionInterceptor permissionInterceptor(Map<String, IPermissionValidator> map) {
        return new ApiPermissionInterceptor(map);
    }


    @Bean
    public ControllerAspect controllerAspect(final Map<String, IApiInterceptor> map) {
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
    @ConditionalOnMissingBean(ITokenParser.class)
    @ConditionalOnBean(IUserModelPicker.class)
    public ITokenParser userModelTokenParser() {
        return new UserModelTokenParser();
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
