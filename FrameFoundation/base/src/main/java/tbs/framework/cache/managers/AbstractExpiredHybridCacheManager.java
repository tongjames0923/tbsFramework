package tbs.framework.cache.managers;

import org.jetbrains.annotations.NotNull;
import tbs.framework.cache.ICacheService;
import tbs.framework.cache.hooks.IHybridCacheServiceHook;
import tbs.framework.cache.supports.ICacheServiceHybridSupport;
import tbs.framework.utils.BeanUtil;

import java.time.Duration;
import java.util.ArrayList;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

/**
 * The type Abstract timebase hybrid cache manager.
 *
 * @author abstergo
 */
public abstract class AbstractExpiredHybridCacheManager extends AbstractExpireManager
    implements ICacheServiceHybridSupport {
    private int m_serviceIndex = 0;
    private ArrayList<ICacheService> cacheServiceArrayList = new ArrayList<>(8);

    @Override
    public int serviceIndex() {
        return m_serviceIndex;
    }

    @Override
    public void setService(int index) {
        if (index >= cacheServiceArrayList.size()) {
            throw new ArrayIndexOutOfBoundsException("index is too large");
        }
        foreachHook((h) -> {
            IHybridCacheServiceHook hook = BeanUtil.getAs(h);
            hook.onSwitch(this, cacheServiceArrayList.get(m_serviceIndex), cacheServiceArrayList.get(index));
        }, IHybridCacheServiceHook.HOOK_OPERATE_SWITCH);

        this.m_serviceIndex = index;
    }

    @Override
    public int selectService(BiPredicate<ICacheService, Integer> condition) {
        int i = -1;
        for (ICacheService cacheService : cacheServiceArrayList) {
            if (condition.test(cacheService, ++i)) {
                break;
            }
        }
        return i;
    }

    @Override
    public void addService(@NotNull ICacheService service) {
        foreachHook((h) -> {
            IHybridCacheServiceHook hook = BeanUtil.getAs(h);
            hook.onNewCacheServiceAdd(this, service, cacheServiceArrayList.size());
        }, IHybridCacheServiceHook.HOOK_OPERATE_ADD_SERVICE);
        cacheServiceArrayList.add(service);
    }

    @Override
    public void removeService(int index) {
        foreachHook((h) -> {
            IHybridCacheServiceHook hook = BeanUtil.getAs(h);
            hook.onServiceRemove(this, cacheServiceArrayList.get(index), cacheServiceArrayList.size());
        }, IHybridCacheServiceHook.HOOK_OPERATE_REMOVE_SERVICE);
        cacheServiceArrayList.remove(index);

    }

    @Override
    public int serviceCount() {
        return cacheServiceArrayList.size();
    }

    @Override
    public ICacheService getCacheService() {
        return cacheServiceArrayList.get(serviceIndex());
    }


    @Override
    public void operateCacheService(@NotNull int index, @NotNull Consumer<ICacheService> operation) {
        ICacheService service = cacheServiceArrayList.get(index);
        operation.accept(service);
    }

    @Override
    public void clear() {
        hookForClear();
        clearImpl();
    }

    @Override
    public long size() {
        Long[] r = new Long[] {0L};
        selectService((c, i) -> {
            r[0] += c.cacheSize();
            return false;
        });
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
        putImpl(key, value, override);
    }

    @Override
    public Object get(String key) {
        Object r = getImpl(key);
        hookForGet(key);
        return r;
    }

    @Override
    public boolean exists(String key) {
        boolean r = existsImpl(key);
        hookForExist(key);
        return r;
    }

    @Override
    public void remove(String key) {
        hookForRemove(key);
        removeImpl(key);
    }

    @Override
    public void expire(String key, Duration time) {
        hookForExpire(key, time);
        selectService((c, i) -> {
            if (c.exists(key)) {
                getExpireSupportOrThrows(c).expire(key, time, this, c);
            }
            return false;
        });
    }

    @Override
    public Duration remaining(String key) {
        Long[] r = new Long[] {Long.MAX_VALUE};
        selectService((c, i) -> {
            long v = getExpireSupportOrThrows(c).remaining(key, this, c);
            r[0] = Math.min(v, r[0]);
            return false;
        });
        return Duration.ofMillis(r[0]);
    }
}
