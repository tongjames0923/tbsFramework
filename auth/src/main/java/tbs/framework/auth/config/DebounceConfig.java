package tbs.framework.auth.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import tbs.framework.auth.interfaces.IApiInterceptor;
import tbs.framework.auth.interfaces.debounce.IDebounce;
import tbs.framework.auth.interfaces.debounce.impls.AESTokenDebounce;
import tbs.framework.auth.interfaces.debounce.impls.CacheDebounce;
import tbs.framework.auth.interfaces.debounce.impls.DebounceInterceptor;
import tbs.framework.auth.properties.AuthProperty;
import tbs.framework.auth.properties.DebounceProperty;
import tbs.framework.cache.managers.AbstractExpireManager;

import javax.annotation.Resource;

/**
 * @author abstergo
 */
public class DebounceConfig {

    @Resource
    AuthProperty authProperty;

    @Resource
    DebounceProperty debounceProperty;

    @Bean
    IApiInterceptor debounceInterceptor(IDebounce debounce) {
        return new DebounceInterceptor(debounce, debounceProperty);
    }

    @Bean
    @ConditionalOnMissingBean({IDebounce.class, AbstractExpireManager.class})
    AESTokenDebounce simpleDefaultDebounce() {
        return new AESTokenDebounce(authProperty.getApiStabilizationField(), debounceProperty.getApiColdDownTime());
    }

    @Bean
    @ConditionalOnMissingBean(IDebounce.class)
    @ConditionalOnBean(AbstractExpireManager.class)
    CacheDebounce cacheDebounce(AbstractExpireManager expireManager) {
        return new CacheDebounce(expireManager, debounceProperty.getApiColdDownTime());
    }
}
