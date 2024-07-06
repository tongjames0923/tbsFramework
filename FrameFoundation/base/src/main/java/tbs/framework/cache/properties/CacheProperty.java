package tbs.framework.cache.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import tbs.framework.cache.strategy.AbstractTimeBaseCacheEliminationStrategy;
import tbs.framework.cache.ICacheAspectJudgeMaker;
import tbs.framework.cache.ICacheService;
import tbs.framework.cache.impls.eliminate.judges.makers.KeyExpiredCacheAspectJudgeMaker;
import tbs.framework.cache.impls.eliminations.strategys.ExpiredCacheElimination;
import tbs.framework.cache.impls.services.ConcurrentMapCacheServiceImpl;

@Data
@ConfigurationProperties(prefix = "tbs.framework.cache")
public class CacheProperty {
    /**
     * 缓存服务是否接收空值
     */
    private boolean acceptNullValues = false;

    /**
     * 默认的主要缓存服务
     */
    private Class<? extends ICacheService> cacheServiceClass = ConcurrentMapCacheServiceImpl.class;

    /**
     * 缓存清除判官生成器
     */
    private Class<? extends ICacheAspectJudgeMaker> cacheKillJudgeMaker = KeyExpiredCacheAspectJudgeMaker.class;
    /**
     * 缓存清除策略
     */
    private Class<? extends AbstractTimeBaseCacheEliminationStrategy> cacheKillStrategy = ExpiredCacheElimination.class;

}
