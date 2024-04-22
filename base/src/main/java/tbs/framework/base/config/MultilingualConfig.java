package tbs.framework.base.config;

import cn.hutool.extra.spring.SpringUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import tbs.framework.base.log.ILogger;
import tbs.framework.base.multilingaul.ILocal;
import tbs.framework.base.multilingaul.aspects.MultilingualAspect;
import tbs.framework.base.multilingaul.impls.LocalStringTranslateImpl;
import tbs.framework.base.properties.LocalProperty;
import tbs.framework.base.utils.LogUtil;
import tbs.framework.base.utils.MultilingualUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class MultilingualConfig implements WebMvcConfigurer {
    private static ILogger log;

    public MultilingualConfig(LogUtil logUtil) {
        if (log == null) {
            log = logUtil.getLogger(MultilingualConfig.class.getName());
        }
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        List<HandlerInterceptor> list = new LinkedList<>(SpringUtil.getBeansOfType(HandlerInterceptor.class).values());
        for (HandlerInterceptor interceptor : list) {
            registry.addInterceptor(interceptor);
        }
        WebMvcConfigurer.super.addInterceptors(registry);
    }

    @Bean
    @ConditionalOnMissingBean(ILocal.class)
    public ILocal defaultLocal(LogUtil logUtil, MultilingualUtil multilingualUtil) {
        return new LocalStringTranslateImpl(logUtil, multilingualUtil);
    }

    @Bean
    public HandlerInterceptor localeChangeInterceptor(LocalProperty localProperty, LocaleResolver localeResolver) {
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

    @Bean
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
                        //                        request.getParameterMap().put(localProperty.getValue(), new String[] {locale.getLanguage()});
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
