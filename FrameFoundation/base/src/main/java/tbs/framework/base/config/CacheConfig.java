package tbs.framework.base.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import tbs.framework.base.constants.BeanNameConstant;
import tbs.framework.cache.ICacheAspectJudgeMaker;
import tbs.framework.cache.ICacheService;
import tbs.framework.cache.IExpireable;
import tbs.framework.cache.aspects.CacheAspect;
import tbs.framework.cache.impls.managers.ImportedTimeBaseCacheManager;
import tbs.framework.cache.impls.services.ConcurrentMapCacheServiceImpl;
import tbs.framework.cache.managers.AbstractTimeBaseCacheManager;
import tbs.framework.cache.properties.CacheProperty;
import tbs.framework.cache.strategy.AbstractTimeBaseCacheEliminationStrategy;

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

    @Bean
    @ConditionalOnMissingBean(AbstractTimeBaseCacheManager.class)
    AbstractTimeBaseCacheManager timeBaseCacheManager(
        @Qualifier(BeanNameConstant.BUILTIN_CACHE_SERVICE) ICacheService timeBaseCacheService, IExpireable hook) {
        return new ImportedTimeBaseCacheManager(timeBaseCacheService, hook);
    }

}
