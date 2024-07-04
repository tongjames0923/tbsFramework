package tbs.framework.cache.impls;

import org.springframework.scheduling.annotation.Scheduled;
import tbs.framework.cache.ICacheService;
import tbs.framework.cache.IkeyMixer;
import tbs.framework.lock.impls.SimpleLockAddtionalInfo;
import tbs.framework.log.ILogger;
import tbs.framework.log.annotations.AutoLogger;
import tbs.framework.proxy.IProxy;
import tbs.framework.proxy.impls.LockProxy;
import tbs.framework.base.utils.LogFactory;

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
public class SimpleCacheServiceImpl implements ICacheService, IkeyMixer {
    @Override
    public String mixKey(String key) {
        return "SIMPLE_CACHE-" + key;
    }

    private final ConcurrentHashMap<String, Object> cache = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, CacheEntry> delayedCache = new ConcurrentHashMap<>();

    @AutoLogger
    private ILogger logger;

    private IProxy.IProxyAdditionalInfo getLockId() {
        return new SimpleLockAddtionalInfo(this.toString());
    }

    @Resource
    LockProxy lockProxy;

    public static class CacheEntry implements Delayed {

        private String key;
        private long expiration;

        public CacheEntry(final String key, final long expiration) {
            this.key = key;
            this.expiration = expiration;
        }

        public String getKey() {
            return this.key;
        }

        public void setKey(final String key) {
            this.key = key;
        }

        public long getExpiration() {
            return this.expiration;
        }

        public void setExpiration(final long expiration) {
            this.expiration = expiration;
        }

        @Override
        public long getDelay(final TimeUnit unit) {
            return unit.convert(Duration.ofSeconds(expiration).getSeconds() -
                Duration.ofMillis(System.currentTimeMillis()).getSeconds(), TimeUnit.SECONDS);
        }

        @Override
        public int compareTo(final Delayed o) {
            final long diff = getDelay(TimeUnit.SECONDS) - o.getDelay(TimeUnit.SECONDS);
            return Long.compare(diff, 0);
        }
    }

    private final DelayQueue<CacheEntry> expirationQueue = new DelayQueue<>();

    private void inQueue(final CacheEntry c) {
        this.expirationQueue.add(c);
        this.delayedCache.put(c.key, c);
    }

    private CacheEntry outQueue() {
        final CacheEntry cacheEntry = this.expirationQueue.poll();
        if (null != cacheEntry) {
            this.delayedCache.remove(cacheEntry.getKey());
        }
        return cacheEntry;
    }

    /**
     * <p>scheduleExpiration.</p>
     */
    @Scheduled(fixedRate = 500)
    public void scheduleExpiration() {
        if (this.expirationQueue.isEmpty()) {
            return;
        }
        final CacheEntry entry = this.outQueue();
        if (null == entry) {
            return;
        }

        this.lockProxy.safeProxy((p) -> {
            if (!this.cache.containsKey(entry.key)) {
                this.logger.debug("不在缓存中的Key:" + entry.key);
                return null;
            }
            while (null != p) {
                final long delay = p.getDelay(TimeUnit.SECONDS);
                if (0 < delay) {
                    this.inQueue(p);
                    break;
                } else {
                    this.logger.debug(String.format("'%s' cache expired", p.getKey()));
                    this.cache.remove(p.key);
                }
                p = this.outQueue();
            }

            return null;
        }, entry, getLockId());

    }

    /**
     * <p>Constructor for SimpleCacheServiceImpl.</p>
     *
     * @param logUtil a {@link LogFactory} object
     */
    public SimpleCacheServiceImpl() {

    }

    @Override
    public void put(final String key, final Object value, final boolean override) {
        String tk = this.mixKey(key);
        if (this.cache.containsKey(tk) && !override) {
            return;
        }
        this.cache.put(tk, value);
    }

    @Override
    public Optional get(final String key, final boolean isRemove, final long delay) {
        String tk = this.mixKey(key);
        if (this.cache.containsKey(tk)) {
            final Optional result = Optional.ofNullable(this.cache.get(tk));
            if (isRemove && 0 <= delay) {
                if (0 == delay) {
                    this.cache.remove(tk);
                } else {
                    this.expire(tk, delay);
                }
            }
            return result;
        }
        return Optional.empty();
    }

    @Override
    public boolean exists(String key) {
        return cache.containsKey(mixKey(key));
    }

    @Override
    public void remove(final String key) {
        String tk = this.mixKey(key);

        this.expire(tk, 0);
    }

    @Override
    public void clear() {
        this.lockProxy.safeProxy((p -> {
            this.cache.clear();
            this.delayedCache.clear();
            this.expirationQueue.clear();
            return null;
        }), null, getLockId());

    }

    @Override
    public void expire(final String key, final long seconds) {
        String tk = this.mixKey(key);
        final long now = System.currentTimeMillis() / 1000L;
        this.lockProxy.safeProxy((p) -> {
            final CacheEntry entry = new CacheEntry(tk, now + seconds);
            this.inQueue(entry);
            return null;
        }, null, getLockId());


    }

    @Override
    public long remain(final String key) {
        String tk = this.mixKey(key);
        if (this.delayedCache.containsKey(tk)) {
            return this.delayedCache.get(tk).getDelay(TimeUnit.SECONDS);
        }
        return Long.MIN_VALUE;
    }
}
