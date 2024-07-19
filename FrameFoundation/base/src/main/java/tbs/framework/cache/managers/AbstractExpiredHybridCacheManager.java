package tbs.framework.cache.managers;

import org.jetbrains.annotations.NotNull;
import tbs.framework.cache.ICacheService;
import tbs.framework.cache.hooks.IHybridCacheServiceHook;
import tbs.framework.cache.supports.ICacheServiceHybridSupport;
import tbs.framework.lock.IReadWriteLock;
import tbs.framework.proxy.impls.LockProxy;
import tbs.framework.utils.BeanUtil;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiPredicate;

/**
 * The type Abstract timebase hybrid cache manager.
 *
 * @author abstergo
 */
public abstract class AbstractExpiredHybridCacheManager extends AbstractExpireManager
    implements ICacheServiceHybridSupport {
    private int m_serviceIndex = 0;

    @Resource
    LockProxy lockProxy;

    public static final String GLOBAL_LOCK = "GLOBAL_LOCK_CACHE_MANAGER";

    @Resource(name = GLOBAL_LOCK)
    IReadWriteLock globalLock;

    @NotNull
    protected abstract List<ICacheService> getCacheServiceList();

    @Override
    public int serviceIndex() {
        return m_serviceIndex;
    }

    @Override
    public void setService(int index) {
        if (index >= getCacheServiceList().size()) {
            throw new ArrayIndexOutOfBoundsException("index is too large");
        }

        foreachHook((h) -> {
            IHybridCacheServiceHook hook = BeanUtil.getAs(h);
            hook.onSwitch(this, getCacheServiceList().get(m_serviceIndex), getCacheServiceList().get(index));
        }, IHybridCacheServiceHook.HOOK_OPERATE_SWITCH);
        lockProxy.quickLock(() -> {
            m_serviceIndex = index;
        }, globalLock.writeLock());
    }

    @Override
    public int selectService(BiPredicate<ICacheService, Integer> condition) {
        AtomicInteger i = new AtomicInteger(-1);

        for (ICacheService cacheService : getCacheServiceList()) {
            if (condition.test(cacheService, i.incrementAndGet())) {
                break;
            }
        }
        return i.get();
    }

    @Override
    public void addService(@NotNull ICacheService service) {
        foreachHook((h) -> {
            IHybridCacheServiceHook hook = BeanUtil.getAs(h);
            hook.onNewCacheServiceAdd(this, service, getCacheServiceList().size());
        }, IHybridCacheServiceHook.HOOK_OPERATE_ADD_SERVICE);

        lockProxy.quickLock(() -> {
            getCacheServiceList().add(service);
        }, globalLock.writeLock());
    }

    @Override
    public void removeService(int index) {
        foreachHook((h) -> {
            IHybridCacheServiceHook hook = BeanUtil.getAs(h);
            hook.onServiceRemove(this, getCacheServiceList().get(index), getCacheServiceList().size());
        }, IHybridCacheServiceHook.HOOK_OPERATE_REMOVE_SERVICE);
        lockProxy.quickLock(() -> {
            getCacheServiceList().remove(index);
        }, globalLock.writeLock());

    }

    @Override
    public int serviceCount() {
        return getCacheServiceList().size();
    }

    @Override
    public ICacheService getCacheService() {
        return getCacheServiceList().get(serviceIndex());
    }

    @Override
    public void clear() {
        hookForClear();
        lockProxy.quickLock(() -> {
            clearImpl();
        }, globalLock.writeLock());
    }

    @Override
    public long size() {
        Long[] r = new Long[] {0L};
        lockProxy.quickLock(() -> {
            selectService((c, i) -> {
                r[0] += c.cacheSize();
                return false;
            });
        }, globalLock.readLock());
        return r[0];
    }

    /**
     * 设置缓存的实现
     *
     * @param key   the key
     * @param value the value
     * @param ov    the ov
     */
    protected abstract void putImpl(String key, Object value, boolean ov);

    /**
     * 获取缓存的实现
     *
     * @param key the key
     * @return the
     */
    protected abstract Object getImpl(String key);

    /**
     * 测试是否存在的实现
     *
     * @param key the key
     * @return the boolean
     */
    protected abstract boolean existsImpl(String key);

    /**
     * 移除缓存的实现
     *
     * @param key the key
     */
    protected abstract void removeImpl(String key);

    /**
     * 清空缓存的实现
     */
    protected abstract void clearImpl();

    @Override
    public void put(String key, Object value, boolean override) {
        hookForPut(key, value, override);
        lockProxy.quickLock(() -> {
            putImpl(key, value, override);
        }, globalLock.writeLock());

    }

    @Override
    public Object get(String key) {
        Object[] r = new Object[] {null};
        lockProxy.quickLock(() -> {
            r[0] = getImpl(key);
        }, globalLock.readLock());

        hookForGet(key);
        return r[0];
    }

    @Override
    public boolean exists(String key) {
        Boolean[] r = new Boolean[] {false};
        lockProxy.quickLock(() -> {
            r[0] = existsImpl(key);
        }, globalLock.readLock());
        hookForExist(key);
        return r[0];
    }

    @Override
    public void remove(String key) {
        hookForRemove(key);
        removeImpl(key);
    }

    @Override
    public void expire(String key, Duration time) {
        hookForExpire(key, time);
        lockProxy.quickLock(() -> {
            selectService((c, i) -> {
                getExpireSupportOrThrows(c).expire(key, time, this, c);
                return false;
            });
        }, globalLock.writeLock());

    }

    @Override
    public Duration remaining(String key) {
        Long[] r = new Long[] {Long.MAX_VALUE};
        lockProxy.quickLock(() -> {
            selectService((c, i) -> {
                long v = getExpireSupportOrThrows(c).remaining(key, this, c);
                r[0] = Math.min(v, r[0]);
                return false;
            });
        }, globalLock.readLock());

        return Duration.ofMillis(r[0]);
    }

    @Override
    public void ifExsist(String key, boolean expectNotExist, boolean isWriteOperation,
        ICacheExistOpearte cacheExistOpearte) {
        lockProxy.quickLock(() -> {
            boolean e = existsImpl(key);
            if (e == expectNotExist) {
                cacheExistOpearte.accept(key, this);
            }
        }, isWriteOperation ? globalLock.writeLock() : globalLock.readLock());
    }
}
