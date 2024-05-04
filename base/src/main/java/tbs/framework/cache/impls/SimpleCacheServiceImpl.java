package tbs.framework.cache.impls;

import org.springframework.scheduling.annotation.Scheduled;
import tbs.framework.base.lock.impls.SimpleLockAddtionalInfo;
import tbs.framework.base.log.ILogger;
import tbs.framework.base.proxy.IProxy;
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

    private IProxy.IProxyAddtionalInfo getLockId() {
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
     * @param logUtil a {@link tbs.framework.base.utils.LogUtil} object
     */
    public SimpleCacheServiceImpl(final LogUtil logUtil) {
        logger = logUtil.getLogger(SimpleCacheServiceImpl.class.getName());
    }

    @Override
    public void put(final String key, final Object value, final boolean override) {
        if (this.cache.containsKey(key) && !override) {
            return;
        }
        this.cache.put(key, value);
    }

    @Override
    public Optional get(final String key, final boolean isRemove, final long delay) {
        if (this.cache.containsKey(key)) {
            final Optional result = Optional.ofNullable(this.cache.get(key));
            if (isRemove && 0 <= delay) {
                if (0 == delay) {
                    this.cache.remove(key);
                } else {
                    this.expire(key, delay);
                }
            }
            return result;
        }
        return Optional.empty();
    }

    @Override
    public void remove(final String key) {
        this.expire(key, 0);
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
        final long now = System.currentTimeMillis() / 1000L;
        this.lockProxy.safeProxy((p) -> {
            final CacheEntry entry = new CacheEntry(key, now + seconds);
            this.inQueue(entry);
            return null;
        }, null, getLockId());


    }

    @Override
    public long remain(final String key) {
        if (this.delayedCache.containsKey(key)) {
            return this.delayedCache.get(key).getDelay(TimeUnit.SECONDS);
        }
        return Long.MIN_VALUE;
    }
}
