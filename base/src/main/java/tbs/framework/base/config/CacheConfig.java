package tbs.framework.base.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import tbs.framework.base.utils.LogUtil;
import tbs.framework.cache.ICacheService;
import tbs.framework.cache.impls.SimpleCacheServiceImpl;

public class CacheConfig {

    @Bean
    @ConditionalOnMissingBean(ICacheService.class)
    public ICacheService cacheService(final LogUtil logUtil) {
        return new SimpleCacheServiceImpl(logUtil);
    }
}
