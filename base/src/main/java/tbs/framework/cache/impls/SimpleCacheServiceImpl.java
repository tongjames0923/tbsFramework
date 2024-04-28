package tbs.framework.cache.impls;

import org.springframework.scheduling.annotation.Scheduled;
import tbs.framework.base.log.ILogger;
import tbs.framework.base.proxy.impls.LockProxy;
import tbs.framework.base.utils.LogUtil;
import tbs.framework.cache.ICacheService;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * <p>SimpleCacheServiceImpl class.</p>
 *
 * @author abstergo
 * @version $Id: $Id
 */
public class SimpleCacheServiceImpl implements ICacheService {

    private final ConcurrentHashMap<String, Object> cache = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, CacheEntry> delayedCache = new ConcurrentHashMap<>();


    private final ILogger logger;

    @Resource
    LockProxy lockProxy;

    public static class CacheEntry implements Delayed {

        private String key;
        private long expiration;

        public CacheEntry(String key, long expiration) {
            this.key = key;
            this.expiration = expiration;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public long getExpiration() {
            return expiration;
        }

        public void setExpiration(long expiration) {
            this.expiration = expiration;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(Duration.ofSeconds(this.expiration).getSeconds() -
                Duration.ofMillis(System.currentTimeMillis()).getSeconds(), TimeUnit.SECONDS);
        }

        @Override
        public int compareTo(Delayed o) {
            long diff = this.getDelay(TimeUnit.SECONDS) - o.getDelay(TimeUnit.SECONDS);
            return Long.compare(diff, 0);
        }
    }

    private final DelayQueue<CacheEntry> expirationQueue = new DelayQueue<>();

    private void inQueue(CacheEntry c) {
        expirationQueue.add(c);
        delayedCache.put(c.key, c);
    }

    private CacheEntry outQueue() {
        CacheEntry cacheEntry = expirationQueue.poll();
        if (null != cacheEntry) {
            delayedCache.remove(cacheEntry.getKey());
        }
        return cacheEntry;
    }

    /**
     * <p>scheduleExpiration.</p>
     */
    @Scheduled(fixedRate = 500)
    public void scheduleExpiration() {
        if (expirationQueue.isEmpty()) {
            return;
        }
        CacheEntry entry = outQueue();
        if (null == entry) {
            return;
        }

        lockProxy.safeProxy((p) -> {
            if (!cache.containsKey(entry.key)) {
                logger.debug("不在缓存中的Key:" + entry.key);
                return null;
            }
            while (null != p) {
                long delay = p.getDelay(TimeUnit.SECONDS);
                if (0 < delay) {
                    inQueue(p);
                    break;
                } else {
                    logger.debug(String.format("'%s' cache expired", p.getKey()));
                    cache.remove(p.key);
                }
                p = outQueue();
            }

            return null;
        }, entry);

    }

    /**
     * <p>Constructor for SimpleCacheServiceImpl.</p>
     *
     * @param logUtil a {@link tbs.framework.base.utils.LogUtil} object
     */
    public SimpleCacheServiceImpl(LogUtil logUtil) {
        this.logger = logUtil.getLogger(SimpleCacheServiceImpl.class.getName());
    }

    /** {@inheritDoc} */
    @Override
    public void put(String key, Object value, boolean override) {
        if (cache.containsKey(key) && !override) {
            return;
        }
        cache.put(key, value);
    }

    /** {@inheritDoc} */
    @Override
    public Optional get(String key, boolean isRemove, long delay) {
        if (cache.containsKey(key)) {
            Optional result = Optional.ofNullable(cache.get(key));
            if (isRemove && 0 <= delay) {
                if (0 == delay) {
                    cache.remove(key);
                } else {
                    expire(key, delay);
                }
            }
            return result;
        }
        return Optional.empty();
    }

    /** {@inheritDoc} */
    @Override
    public void remove(String key) {
        expire(key, 0);
    }

    /** {@inheritDoc} */
    @Override
    public void clear() {
        lockProxy.safeProxy((p -> {
            cache.clear();
            delayedCache.clear();
            expirationQueue.clear();
            return null;
        }), null);

    }

    /** {@inheritDoc} */
    @Override
    public void expire(String key, long seconds) {
        long now = System.currentTimeMillis() / 1000L;
        lockProxy.safeProxy((p) -> {
            CacheEntry entry = new CacheEntry(key, now + seconds);
            inQueue(entry);
            return null;
        }, null);


    }

    /** {@inheritDoc} */
    @Override
    public long remain(String key) {
        if (delayedCache.containsKey(key)) {
            return delayedCache.get(key).getDelay(TimeUnit.SECONDS);
        }
        return Long.MIN_VALUE;
    }
}
