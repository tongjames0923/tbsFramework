package tbs.framework.base.config;

import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import tbs.framework.base.constants.BeanNameConstant;
import tbs.framework.log.ILogger;
import tbs.framework.base.properties.LocalProperty;
import tbs.framework.utils.LogUtil;
import tbs.framework.utils.MultilingualUtil;
import tbs.framework.multilingual.ILocal;
import tbs.framework.multilingual.aspects.MultilingualAspect;
import tbs.framework.multilingual.impls.LocalStringTranslateImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

/**
 * @author abstergo
 */
public class MultilingualConfig {
    private static ILogger log;

    public MultilingualConfig(final LogUtil logUtil) {
        if (null == log) {
            MultilingualConfig.log = logUtil.getLogger(MultilingualConfig.class.getName());
        }
    }

    @Bean
    WebMvcConfigurer multilingualConfigurer(final LocalProperty localProperty,
        @Qualifier(BeanNameConstant.LOCALE_CHANGE_INTERCEPTOR) final HandlerInterceptor handlerInterceptor) {
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(final InterceptorRegistry registry) {
                registry.addInterceptor(handlerInterceptor).addPathPatterns(localProperty.getPathPattern());
                WebMvcConfigurer.super.addInterceptors(registry);
            }
        };
    }


    @Bean
    @ConditionalOnMissingBean(ILocal.class)
    public ILocal defaultLocal(final LogUtil logUtil, final MultilingualUtil multilingualUtil) {
        return new LocalStringTranslateImpl(logUtil, multilingualUtil);
    }

    @Bean(BeanNameConstant.LOCALE_CHANGE_INTERCEPTOR)
    public HandlerInterceptor localeChangeInterceptor(final LocalProperty localProperty,
        @Qualifier(BeanNameConstant.BUILTIN_LOCALE_RESOLVER) final LocaleResolver localeResolver) {
        return new HandlerInterceptor() {
            @Override
            public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler)
                throws Exception {
                final Locale locale = localeResolver.resolveLocale(request);
                localeResolver.setLocale(request, response, locale);
                return HandlerInterceptor.super.preHandle(request, response, handler);
            }
        };
    }

    private String getOrDefault(String value, final String defaultValue) {
        if (StrUtil.isEmpty(value)) {
            value = defaultValue;
        }
        return value;
    }

    @Bean(BeanNameConstant.BUILTIN_LOCALE_RESOLVER)
    @ConditionalOnMissingBean(name = BeanNameConstant.BUILTIN_LOCALE_RESOLVER)
    public LocaleResolver localeResolver(final LocalProperty localProperty) {
        switch (localProperty.getType()) {
            case Parameter:
                return new LocaleResolver() {
                    @Override
                    public Locale resolveLocale(final HttpServletRequest request) {
                        return new Locale(
                            getOrDefault(request.getParameter(localProperty.getValue()), "zh"));
                    }

                    @Override
                    public void setLocale(final HttpServletRequest request, final HttpServletResponse response, final Locale locale) {
                        LocaleContextHolder.setLocale(locale);
                    }
                };
            case Cookie:
                final CookieLocaleResolver cookieLocaleResolver = new CookieLocaleResolver();
                cookieLocaleResolver.setCookieMaxAge(1000);
                cookieLocaleResolver.setCookieName(localProperty.getValue());
                return cookieLocaleResolver;
            case Header:
                return new LocaleResolver() {
                    @Override
                    public Locale resolveLocale(final HttpServletRequest request) {
                        return new Locale(
                            MultilingualConfig.this.getOrDefault(request.getHeader(localProperty.getValue()), "zh"));
                    }

                    @Override
                    public void setLocale(final HttpServletRequest request, final HttpServletResponse response, final Locale locale) {
                        LocaleContextHolder.setLocale(locale);
                    }
                };
            default:
                return null;
        }
    }

    @Bean
    public MultilingualAspect aspect() {
        return new MultilingualAspect();
    }

    @Bean
    public MultilingualUtil multilingualUtil(final LogUtil logUtil) {
        return new MultilingualUtil(logUtil);
    }
}
