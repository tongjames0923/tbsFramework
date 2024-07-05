package tbs.framework.base.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import tbs.framework.base.constants.BeanNameConstant;
import tbs.framework.cache.ICacheService;
import tbs.framework.cache.ITimeBaseSupportedHook;
import tbs.framework.cache.aspects.CacheAspect;
import tbs.framework.cache.impls.eliminate.ExpireCacheStrategy;
import tbs.framework.cache.impls.managers.ImportedTimeBaseCacheManager;
import tbs.framework.cache.impls.services.ConcurrentMapCacheServiceImpl;
import tbs.framework.cache.properties.CacheProperty;

import javax.annotation.Resource;

public class CacheConfig {

    @Resource
    CacheProperty cacheProperty;

    @Bean(BeanNameConstant.BUILTIN_CACHE_SERVICE)
    public ICacheService defaultCacheService() throws Exception {
        if (cacheProperty.getCacheServiceClass() != null) {
            return cacheProperty.getCacheServiceClass().getConstructor().newInstance();
        } else {
            return new ConcurrentMapCacheServiceImpl();
        }
    }

    @Bean
    CacheAspect cacheAspect() {
        return new CacheAspect();
    }

    //TODO 无法自定义Hook
    @Bean
    AbstractTimeBaseCacheManager timeBaseCacheManager(
        @Qualifier(BeanNameConstant.BUILTIN_CACHE_SERVICE) ICacheService timeBaseCacheService,
        ITimeBaseSupportedHook hook) {
        return new ImportedTimeBaseCacheManager(timeBaseCacheService, hook);
    }

    @Bean
    ExpireCacheStrategy expiredStrategy() {
        return new ExpireCacheStrategy();
    }
}
