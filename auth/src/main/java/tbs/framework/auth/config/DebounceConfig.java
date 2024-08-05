package tbs.framework.auth.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import tbs.framework.auth.interfaces.IApiInterceptor;
import tbs.framework.auth.interfaces.debounce.IDebounce;
import tbs.framework.auth.interfaces.debounce.impls.AESTokenDebounce;
import tbs.framework.auth.interfaces.debounce.impls.DebounceInterceptor;
import tbs.framework.auth.properties.AuthProperty;

import javax.annotation.Resource;

public class DebounceConfig {

    @Resource
    AuthProperty authProperty;

    @Bean
    IApiInterceptor debounceInterceptor(IDebounce debounce) {
        return new DebounceInterceptor(debounce);
    }

    @Bean
    @ConditionalOnMissingBean(IDebounce.class)
    AESTokenDebounce simpleDefaultDebounce() {
        return new AESTokenDebounce(authProperty.getApiStabilizationField(), authProperty.getApiColdDownTime());
    }
}
