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

    private static ConcurrentHashMap<String, Object> cache = new ConcurrentHashMap<>();

    private static ConcurrentHashMap<String, CacheEntry> delayedCache = new ConcurrentHashMap<>();

    private static long TOTAL_BEANS = 0;

    private long index = 0;

    private long scheduledCnt = 0;

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

    @Scheduled(fixedRate = 500)
    public void scheduleExpiration() {
        if (TOTAL_BEANS != index) {
            return;
        }
        if (expirationQueue.isEmpty()) {
            return;
        }
        CacheEntry entry = expirationQueue.poll();
        if (entry == null) {
            return;
        }
        lockProxy.safeProxy((p) -> {
            long delay = entry.getDelay(TimeUnit.SECONDS);
            if (delay > 0) {
                expirationQueue.offer(entry);
            } else {
                logger.info(String.format("%s cache expired", entry.getKey()));
                cache.remove(entry.key);
                delayedCache.remove(entry.key);
            }
            return null;
        }, null);
    }

    public SimpleCacheServiceImpl(LogUtil logUtil) {
        if (TOTAL_BEANS >= 8L) {
            throw new ArrayIndexOutOfBoundsException("Cache service has been exhausted");
        }
        this.logger = logUtil.getLogger(SimpleCacheServiceImpl.class.getName());
        TOTAL_BEANS++;
        index = TOTAL_BEANS;
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
        if (cache.containsKey(key)) {
            cache.remove(key);
        }
    }

    @Override
    public void clear() {
        cache.clear();
    }

    @Override
    public void expire(String key, long seconds) {
        long now = System.currentTimeMillis() / 1000L;
        CacheEntry entry = new CacheEntry(key, now + seconds);
        expirationQueue.add(entry);
        delayedCache.put(key, entry);
    }

    @Override
    public long remain(String key) {
        if (delayedCache.containsKey(key)) {
            return delayedCache.get(key).getDelay(TimeUnit.SECONDS);
        }
        return Long.MIN_VALUE;
    }
}
