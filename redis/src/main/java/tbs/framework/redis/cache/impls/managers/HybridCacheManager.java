package tbs.framework.redis.cache.impls.managers;

import org.jetbrains.annotations.NotNull;
import tbs.framework.cache.ICacheService;
import tbs.framework.cache.IExpireable;
import tbs.framework.cache.impls.LocalExpiredImpl;
import tbs.framework.cache.managers.AbstractCacheManager;
import tbs.framework.cache.managers.AbstractTimebaseHybridCacheManager;
import tbs.framework.log.ILogger;
import tbs.framework.log.annotations.AutoLogger;
import tbs.framework.redis.IRedisTemplateSupport;
import tbs.framework.redis.cache.impls.RedisExpiredImpl;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

/**
 * The type Hybrid cache manager.
 *
 * @author abstergo
 */
public class HybridCacheManager extends AbstractTimebaseHybridCacheManager {

    private AtomicLong cleanCacheCount = new AtomicLong(0);

    private static final long LIMIT = 32L;

    @AutoLogger
    private ILogger logger;

    private long levelRatio = 8;

    /**
     * 每层缓存的容量倍数
     *
     * @return the long
     */
    public long levelRatio() {
        return levelRatio;
    }

    /**
     * 每层缓存的容量倍数
     *
     * @param levelRatio the level ratio
     * @return the level ratio
     */
    public HybridCacheManager setLevelRatio(long levelRatio) {
        this.levelRatio = levelRatio;
        return this;
    }

    public HybridCacheManager(ICacheService... services) {
        for (ICacheService service : services) {
            if (service != null) {
                addService(service);
            }
        }
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

    /**
     * @implNote
     * TODO
     * 多线程场景下数据不精确
     */
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

    /**
     * 超时实现
     */
    IExpireable p = new IExpireable() {

        private LocalExpiredImpl localExpired = new LocalExpiredImpl();
        private RedisExpiredImpl redisExpired = new RedisExpiredImpl();

        private void expireByType(@NotNull String key, @NotNull Duration duration,
            @NotNull AbstractCacheManager manager, @NotNull ICacheService cacheService) {
            if (cacheService instanceof IRedisTemplateSupport) {
                redisExpired.expire(key, duration, manager, cacheService);
            } else {
                localExpired.expire(key, duration, manager, cacheService);
            }
        }

        private long remainByType(@NotNull String key, @NotNull AbstractCacheManager manager, @NotNull ICacheService cacheService) {

            if (cacheService instanceof IRedisTemplateSupport) {
                return redisExpired.remaining(key, manager, cacheService);
            } else {
                return localExpired.remaining(key, manager, cacheService);
            }
        }

        @Override
        public void expire(@NotNull String key, @NotNull Duration duration, @NotNull AbstractCacheManager manager, @NotNull ICacheService cacheService) {

            expireByType(key, duration, manager, cacheService);

        }

        @Override
        public long remaining(@NotNull String key, @NotNull AbstractCacheManager manager, @NotNull ICacheService cacheService) {

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
