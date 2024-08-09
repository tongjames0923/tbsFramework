package tbs.framework.base.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import tbs.framework.base.constants.BeanNameConstant;
import tbs.framework.cache.ICacheAspectJudgeMaker;
import tbs.framework.cache.ICacheService;
import tbs.framework.cache.IExpireable;
import tbs.framework.cache.aspects.CacheAspect;
import tbs.framework.cache.impls.LocalExpiredImpl;
import tbs.framework.cache.impls.managers.ImportedExpireManager;
import tbs.framework.cache.impls.services.ConcurrentMapCacheServiceImpl;
import tbs.framework.cache.managers.AbstractCacheManager;
import tbs.framework.cache.managers.AbstractExpireManager;
import tbs.framework.cache.properties.CacheProperty;
import tbs.framework.cache.strategy.AbstractCacheEliminationStrategy;

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
    CacheAspect cacheAspect(AbstractCacheManager cacheManager) {
        return new CacheAspect(cacheManager);
    }

    @Bean
    ICacheAspectJudgeMaker judgeMaker() throws Exception {
        return cacheProperty.getCacheKillJudgeMaker().getConstructor().newInstance();
    }

    @Bean
    AbstractCacheEliminationStrategy defaultTimeBaseCacheEliminationStrategy() throws Exception {
        return cacheProperty.getCacheKillStrategy().getConstructor().newInstance();
    }

    @Bean
    @ConditionalOnMissingBean(AbstractExpireManager.class)
    AbstractCacheManager timeBaseCacheManager(
        @Qualifier(BeanNameConstant.BUILTIN_CACHE_SERVICE) ICacheService timeBaseCacheService, IExpireable hook) {
        return new ImportedExpireManager(timeBaseCacheService, hook);
    }

    @Bean
    @ConditionalOnMissingBean(IExpireable.class)
    LocalExpiredImpl localExpireImpl() {
        return new LocalExpiredImpl();
    }
}
