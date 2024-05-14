package tbs.framework.base.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import tbs.framework.utils.LogUtil;
import tbs.framework.cache.ICacheBroker;
import tbs.framework.cache.ICacheService;
import tbs.framework.cache.aspects.CacheAspect;
import tbs.framework.cache.impls.eliminate.ExpireCacheStrategy;
import tbs.framework.cache.impls.broker.NoneNullCacheBroker;
import tbs.framework.cache.impls.broker.NullableCacheBroker;
import tbs.framework.cache.impls.SimpleCacheServiceImpl;

public class CacheConfig {

    @Bean
    @ConditionalOnMissingBean(ICacheService.class)
    public ICacheService cacheService() {
        return new SimpleCacheServiceImpl();
    }

    @Bean
    CacheAspect cacheAspect() {
        return new CacheAspect();
    }

    @Bean
    ICacheBroker nullable() {
        return new NullableCacheBroker();
    }

    @Bean
    ICacheBroker noneNull() {
        return new NoneNullCacheBroker();
    }

    @Bean
    ExpireCacheStrategy expiredStrategy() {
        return new ExpireCacheStrategy();
    }
}
