package tbs.framework.base.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import tbs.framework.base.constants.BeanNameConstant;
import tbs.framework.cache.*;
import tbs.framework.cache.aspects.CacheAspect;
import tbs.framework.cache.impls.managers.ImportedTimeBaseCacheManager;
import tbs.framework.cache.impls.services.ConcurrentMapCacheServiceImpl;
import tbs.framework.cache.properties.CacheProperty;

import javax.annotation.Resource;

/**
 * @author Abstergo
 */
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

    @Bean
    ICacheAspectJudgeMaker judgeMaker() throws Exception {
        return cacheProperty.getCacheKillJudgeMaker().getConstructor().newInstance();
    }

    @Bean
    AbstractTimeBaseCacheEliminationStrategy defaultTimeBaseCacheEliminationStrategy() throws Exception {
        return cacheProperty.getCacheKillStrategy().getConstructor().newInstance();
    }

    //TODO 无法自定义Hook
    @Bean
    AbstractTimeBaseCacheManager timeBaseCacheManager(
        @Qualifier(BeanNameConstant.BUILTIN_CACHE_SERVICE) ICacheService timeBaseCacheService,
        ITimeBaseSupportedHook hook) {
        return new ImportedTimeBaseCacheManager(timeBaseCacheService, hook);
    }

}
