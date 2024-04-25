package tbs.framework.base.config;

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
import tbs.framework.base.log.ILogger;
import tbs.framework.base.properties.LocalProperty;
import tbs.framework.base.utils.LogUtil;
import tbs.framework.base.utils.MultilingualUtil;
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

    public MultilingualConfig(LogUtil logUtil) {
        if (log == null) {
            log = logUtil.getLogger(MultilingualConfig.class.getName());
        }
    }

    @Bean
    WebMvcConfigurer multilingualConfigurer(LocalProperty localProperty,
        @Qualifier(BeanNameConstant.LOCALE_CHANGE_INTERCEPTOR) HandlerInterceptor handlerInterceptor) {
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(handlerInterceptor).addPathPatterns(localProperty.getPathPattern());
                WebMvcConfigurer.super.addInterceptors(registry);
            }
        };
    }


    @Bean
    @ConditionalOnMissingBean(ILocal.class)
    public ILocal defaultLocal(LogUtil logUtil, MultilingualUtil multilingualUtil) {
        return new LocalStringTranslateImpl(logUtil, multilingualUtil);
    }

    @Bean(BeanNameConstant.LOCALE_CHANGE_INTERCEPTOR)
    public HandlerInterceptor localeChangeInterceptor(LocalProperty localProperty,
        @Qualifier(BeanNameConstant.BUILTIN_LOCALE_RESOLVER) LocaleResolver localeResolver) {
        return new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
                throws Exception {
                Locale locale = localeResolver.resolveLocale(request);
                localeResolver.setLocale(request, response, locale);
                return HandlerInterceptor.super.preHandle(request, response, handler);
            }
        };
    }

    @Bean(BeanNameConstant.BUILTIN_LOCALE_RESOLVER)
    @ConditionalOnMissingBean(name = {BeanNameConstant.BUILTIN_LOCALE_RESOLVER})
    public LocaleResolver localeResolver(LocalProperty localProperty) {
        switch (localProperty.getType()) {
            case Parameter:
                return new LocaleResolver() {
                    @Override
                    public Locale resolveLocale(HttpServletRequest request) {
                        String locale = request.getParameter(localProperty.getValue());
                        return new Locale(locale);
                    }

                    @Override
                    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
                        LocaleContextHolder.setLocale(locale);
                    }
                };
            case Cookie:
                CookieLocaleResolver cookieLocaleResolver = new CookieLocaleResolver();
                cookieLocaleResolver.setCookieMaxAge(1000);
                cookieLocaleResolver.setCookieName(localProperty.getValue());
                return cookieLocaleResolver;
            case Header:
                return new LocaleResolver() {
                    @Override
                    public Locale resolveLocale(HttpServletRequest request) {
                        return new Locale(request.getHeader(localProperty.getValue()));
                    }

                    @Override
                    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
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
    public MultilingualUtil multilingualUtil(LogUtil logUtil) {
        return new MultilingualUtil(logUtil);
    }
}
