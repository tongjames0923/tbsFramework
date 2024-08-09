package tbs.framework.redis.cache.impls.managers;

import cn.hutool.core.collection.CollUtil;
import org.jetbrains.annotations.NotNull;
import tbs.framework.cache.ICacheService;
import tbs.framework.cache.IExpireable;
import tbs.framework.cache.impls.LocalExpiredImpl;
import tbs.framework.cache.managers.AbstractCacheManager;
import tbs.framework.cache.managers.AbstractExpiredHybridCacheManager;
import tbs.framework.lock.IReadWriteLock;
import tbs.framework.log.ILogger;
import tbs.framework.log.annotations.AutoLogger;
import tbs.framework.redis.IRedisTemplateSupport;
import tbs.framework.redis.cache.impls.HybirdCacheExipireImpl;
import tbs.framework.redis.cache.impls.RedisExpiredImpl;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * The type Hybrid cache manager.
 *
 * @author abstergo
 */
public class HybridCacheManager extends AbstractExpiredHybridCacheManager {

    private AtomicLong cleanCacheCount = new AtomicLong(0);

    List<ICacheService> cacheServices = new ArrayList<>(8);

    private static final long LIMIT = 32L;

    @AutoLogger
    private ILogger logger;

    private long levelRatio = 8;

    private HybirdCacheExipireImpl hybirdCacheExipire;

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

    public HybridCacheManager(HybirdCacheExipireImpl hybirdCacheExipire, IReadWriteLock readWriteLock,
        ICacheService... services) {
        this(hybirdCacheExipire, readWriteLock, List.of(services));
    }

    public HybridCacheManager(HybirdCacheExipireImpl exipire, IReadWriteLock readWriteLock,
        List<ICacheService> cacheServiceList) {
        super(readWriteLock);
        this.hybirdCacheExipire = exipire;
        if (CollUtil.isEmpty(cacheServiceList)) {
            return;
        }
        getCacheServiceList().addAll(cacheServiceList.stream().filter((r) -> r != null).collect(Collectors.toList()));
    }

    @Override
    protected @NotNull List<ICacheService> getCacheServiceList() {
        return cacheServices;
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
            return getCacheServiceList().get(k).get(key);
        }
        return null;
    }

    @Override
    protected boolean existsImpl(String key) {
        executeCacheClean(true);
        AtomicBoolean f = new AtomicBoolean(false);
        selectService((c, i) -> {
            if (c.exists(key)) {
                f.set(true);
                return true;
            }
            return false;
        });
        return f.get();
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
            c.clear();
            return false;
        });
    }

    @Override
    protected IExpireable getExpireable() {
        return hybirdCacheExipire;
    }

}
