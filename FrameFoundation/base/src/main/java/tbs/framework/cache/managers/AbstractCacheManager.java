package tbs.framework.cache.managers;

import org.jetbrains.annotations.NotNull;
import tbs.framework.cache.hooks.ICacheServiceHook;
import tbs.framework.cache.supports.ICacheServiceSupport;

import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author Abstergo
 */
public abstract class AbstractCacheManager implements ICacheServiceSupport {
    private Set<ICacheServiceHook> hooks = new HashSet<>();
    private PriorityQueue<ICacheServiceHook> queue = new PriorityQueue<>(new Comparator<ICacheServiceHook>() {
        @Override
        public int compare(ICacheServiceHook o1, ICacheServiceHook o2) {
            return o2.getOrder() - o1.getOrder();
        }
    });

    public AbstractCacheManager addHook(ICacheServiceHook hook) {
        if (!hookSupport(hook)) {
            throw new UnsupportedOperationException("this hook can not be supported");
        }
        hooks.add(hook);
        queue.add(hook);
        return this;
    }

    public AbstractCacheManager removeHook(ICacheServiceHook hook) {
        hooks.remove(hook);
        queue.clear();
        for (ICacheServiceHook h : hooks) {
            queue.add(h);
        }
        return this;
    }

    public boolean hookSupport(@NotNull ICacheServiceHook hook) {
        return true;
    }

    public boolean featureSupport(int code) {
        return false;
    }

    public int hookCount() {
        return queue.size();
    }

    protected void foreachHook(Consumer<ICacheServiceHook> c, int e) {
        for (ICacheServiceHook hook : queue) {
            synchronized (queue) {
                if (hook != null && hook.hookAvaliable(e, this)) {
                    c.accept(hook);
                }
            }
        }
    }

    public void put(String key, Object value, boolean override) {
        hookForPut(key, value, override);
        getCacheService().put(key, value, override);
    }

    protected void hookForPut(String key, Object value, boolean override) {
        foreachHook((hook) -> {
            hook.onSetCache(key, value, override, this);
        }, ICacheServiceHook.OPERATE_FLAG_SET);
    }

    public void put(String key, Object value) {
        put(key, value, true);
    }

    public Object get(String key) {
        hookForGet(key);
        return getCacheService().get(key);
    }

    protected void hookForGet(String key) {
        foreachHook((hook) -> {
            hook.onGetCache(key, this);
        }, ICacheServiceHook.OPERATE_FLAG_GET);
    }

    public boolean exists(String key) {
        hookForExist(key);
        return getCacheService().exists(key);
    }

    protected void hookForExist(String key) {
        foreachHook((hook) -> {
            hook.onTestCache(key, this);
        }, ICacheServiceHook.OPERATE_FLAG_TEST);
    }

    public void remove(String key) {

        hookForRemove(key);

        getCacheService().remove(key);
    }

    protected void hookForRemove(String key) {
        foreachHook((hook) -> {
            hook.onRemoveCache(key, this);
        }, ICacheServiceHook.OPERATE_FLAG_REMOVE);
    }

    public void clear() {
        hookForClear();
        getCacheService().clear();
    }

    protected void hookForClear() {
        foreachHook((hook) -> {
            hook.onClearCache(this);
        }, ICacheServiceHook.OPERATE_FLAG_CLEAR);
    }

    public long size() {
        return getCacheService().cacheSize();
    }

}
