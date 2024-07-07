package tbs.framework.redis.impls.cache.managers;

import cn.hutool.extra.spring.SpringUtil;
import org.jetbrains.annotations.NotNull;
import tbs.framework.cache.ICacheService;
import tbs.framework.cache.IExpireable;
import tbs.framework.cache.constants.CacheServiceTypeCode;
import tbs.framework.cache.impls.LocalExpiredImpl;
import tbs.framework.cache.impls.services.ConcurrentMapCacheServiceImpl;
import tbs.framework.cache.managers.AbstractCacheManager;
import tbs.framework.cache.managers.AbstractTimebaseHybridCacheManager;
import tbs.framework.log.ILogger;
import tbs.framework.log.annotations.AutoLogger;
import tbs.framework.redis.impls.cache.RedisExpiredImpl;
import tbs.framework.redis.impls.cache.services.RedisCacheServiceImpl;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author abstergo
 */
public class HybridCacheManager extends AbstractTimebaseHybridCacheManager {

    private AtomicLong cleanCacheCount = new AtomicLong(0);

    private static final long LIMIT = 32L;

    @AutoLogger
    private ILogger logger;

    private long levelRatio = 8;

    public long levelRatio() {
        return levelRatio;
    }

    public HybridCacheManager setLevelRatio(long levelRatio) {
        this.levelRatio = levelRatio;
        return this;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.addService(SpringUtil.getBean(ConcurrentMapCacheServiceImpl.class));
        this.addService(SpringUtil.getBean(RedisCacheServiceImpl.class));
        setService(0);
    }

    @Override
    public void clear() {
        hookForClear();
        selectService((c, i) -> {
            c.clear();
            logger.debug("clear cache : {} {}", i, c);
            return false;
        });
    }

    @Override
    public long size() {
        Long[] r = new Long[] {0L};
        selectService((c, i) -> {
            long cnt = c.cacheSize();
            r[0] += cnt;
            logger.debug("cache size: {} {} {}", i, c, cnt);
            return false;
        });
        return r[0];
    }

    @Override
    protected void putImpl(String key, Object value, boolean ov) {
        AtomicLong cacheSize = new AtomicLong(1);
        executeCacheClean(false);
        int s = selectService((c, i) -> {
            if (i == this.serviceCount() - 1) {
                logger.debug("level {} cache putted", i);
                c.put(key, value, ov);
                return true;
            } else {
                cacheSize.updateAndGet(v -> v * levelRatio);
                if (c.cacheSize() > cacheSize.get()) {
                    return false;
                }
                logger.debug("level {} cache putted", i);
                c.put(key, value, ov);
                return true;
            }
        });
    }

    private void executeCacheClean(boolean now) {
        if (now) {
            logger.debug("cache clean now");
            getExpireable().execute();
            return;
        }

        long k = cleanCacheCount.updateAndGet(c -> {
            return (c + 1) % LIMIT;
        });
        if (k == 0) {
            logger.debug("cache clean now");
            getExpireable().execute();
        }
    }

    @Override
    protected Object getImpl(String key) {
        executeCacheClean(true);
        int k = selectService((c, i) -> {
            return c.exists(key);
        });

        if (k < serviceCount()) {
            Object[] r = new Object[] {null};
            operateCacheService(k, (s) -> {
                r[0] = s.get(key);
            });
            return r[0];
        }
        return null;
    }

    @Override
    protected boolean existsImpl(String key) {
        executeCacheClean(true);
        int k = selectService((c, i) -> {
            return c.exists(key);
        });
        logger.debug("cache key exists: key={} index={}", key, k);
        if (k >= serviceCount()) {
            return false;
        }
        return true;
    }

    @Override
    protected void removeImpl(String key) {
        executeCacheClean(false);
        selectService((c, i) -> {
            if (c.exists(key)) {
                logger.debug("cache key exists: key={},will removed", key);
                c.remove(key);
            }
            return false;
        });
    }

    @Override
    protected void clearImpl() {
        selectService((c, i) -> {
            operateCacheService(i, (ICacheService s) -> {
                s.clear();
            });
            return false;
        });
    }

    IExpireable p = new IExpireable() {

        private LocalExpiredImpl localExpired = new LocalExpiredImpl();
        private RedisExpiredImpl redisExpired = new RedisExpiredImpl();

        private void expireByType(@NotNull String key, @NotNull Duration duration,
            @NotNull AbstractCacheManager manager, @NotNull ICacheService cacheService) {
            switch (cacheService.serviceType()) {
                case CacheServiceTypeCode.LOCAL:
                    localExpired.expire(key, duration, manager, cacheService);
                    break;
                case CacheServiceTypeCode.REDIS:
                    if (cacheService instanceof RedisCacheServiceImpl) {
                        redisExpired.expire(key, duration, manager, cacheService);
                        break;
                    }
                default:
                    throw new UnsupportedOperationException("未知的服务类型");
            }
        }

        private long remainByType(@NotNull String key, @NotNull AbstractCacheManager manager,
            @NotNull ICacheService cacheService) {
            switch (cacheService.serviceType()) {
                case CacheServiceTypeCode.LOCAL:
                    return localExpired.remaining(key, manager, cacheService);
                case CacheServiceTypeCode.REDIS:
                    if (cacheService instanceof RedisCacheServiceImpl) {
                        return redisExpired.remaining(key, manager, cacheService);
                    }
                default:
                    throw new UnsupportedOperationException("未知的缓存服务类型");
            }
        }

        @Override
        public void expire(@NotNull String key, @NotNull Duration duration, @NotNull AbstractCacheManager manager,
            @NotNull ICacheService cacheService) {

            expireByType(key, duration, manager, cacheService);

        }

        @Override
        public long remaining(@NotNull String key, @NotNull AbstractCacheManager manager,
            @NotNull ICacheService cacheService) {

            return remainByType(key, manager, cacheService);
        }

        @Override
        public void execute() {
            localExpired.execute();
        }
    };

    @Override
    protected IExpireable getExpireable() {
        return p;
    }

}
