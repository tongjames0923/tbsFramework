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

public class SimpleCacheServiceImpl implements ICacheService {

    private ConcurrentHashMap<String, Object> cache = new ConcurrentHashMap<>();

    private ConcurrentHashMap<String, CacheEntry> delayedCache = new ConcurrentHashMap<>();


    private ILogger logger;

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
            return unit.convert(Duration.ofSeconds(getExpiration()).getSeconds() -
                Duration.ofMillis(System.currentTimeMillis()).getSeconds(), TimeUnit.SECONDS);
        }

        @Override
        public int compareTo(Delayed o) {
            long diff = this.getDelay(TimeUnit.SECONDS) - o.getDelay(TimeUnit.SECONDS);
            return Long.compare(diff, 0);
        }
    }

    private DelayQueue<CacheEntry> expirationQueue = new DelayQueue<>();

    private void inQueue(CacheEntry c) {
        expirationQueue.add(c);
        delayedCache.put(c.key, c);
    }

    private CacheEntry outQueue() {
        CacheEntry cacheEntry = expirationQueue.poll();
        if (cacheEntry != null) {
            delayedCache.remove(cacheEntry.getKey());
        }
        return cacheEntry;
    }

    @Scheduled(fixedRate = 500)
    public void scheduleExpiration() {
        if (expirationQueue.isEmpty()) {
            return;
        }
        CacheEntry entry = outQueue();
        if (entry == null) {
            return;
        }

        lockProxy.safeProxy((p) -> {
            if (!cache.containsKey(entry.key)) {
                logger.debug("不在缓存中的Key:" + entry.key);
                return null;
            }
            while (p != null) {
                long delay = p.getDelay(TimeUnit.SECONDS);
                if (delay > 0) {
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

    public SimpleCacheServiceImpl(LogUtil logUtil) {
        this.logger = logUtil.getLogger(SimpleCacheServiceImpl.class.getName());
    }

    @Override
    public void put(String key, Object value, boolean override) {
        if (cache.containsKey(key) && !override) {
            return;
        }
        cache.put(key, value);
    }

    @Override
    public Optional get(String key, boolean isRemove, long delay) {
        if (cache.containsKey(key)) {
            Optional result = Optional.ofNullable(cache.get(key));
            if (isRemove && delay >= 0) {
                if (delay == 0) {
                    cache.remove(key);
                } else {
                    expire(key, delay);
                }
            }
            return result;
        }
        return Optional.empty();
    }

    @Override
    public void remove(String key) {
        expire(key, 0);
    }

    @Override
    public void clear() {
        lockProxy.safeProxy((p -> {
            cache.clear();
            delayedCache.clear();
            expirationQueue.clear();
            return null;
        }), null);

    }

    @Override
    public void expire(String key, long seconds) {
        long now = System.currentTimeMillis() / 1000L;
        lockProxy.safeProxy((p) -> {
            CacheEntry entry = new CacheEntry(key, now + seconds);
            inQueue(entry);
            return null;
        }, null);


    }

    @Override
    public long remain(String key) {
        if (delayedCache.containsKey(key)) {
            return delayedCache.get(key).getDelay(TimeUnit.SECONDS);
        }
        return Long.MIN_VALUE;
    }
}
