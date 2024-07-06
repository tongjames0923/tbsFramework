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

    public void addHook(ICacheServiceHook hook) {
        if (!hookSupport(hook)) {
            throw new UnsupportedOperationException("this hook can not be supported");
        }
        hooks.add(hook);
        queue.add(hook);
    }

    public void removeHook(ICacheServiceHook hook) {
        hooks.remove(hook);
        queue.clear();
        for (ICacheServiceHook h : hooks) {
            queue.add(h);
        }
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
            if (hook != null && hook.hookAvaliable(e, this)) {
                c.accept(hook);
            }
        }
    }

    public void put(String key, Object value, boolean override) {
        getCacheService().put(key, value, override);
        foreachHook((hook) -> {
            hook.onSetCache(key, value, override, getCacheService());
        }, ICacheServiceHook.OPERATE_FLAG_SET);
    }

    public void put(String key, Object value) {
        put(key, value, true);
    }

    public Object get(String key) {
        Object[] val = new Object[] {getCacheService().get(key)};
        foreachHook((hook) -> {
            val[0] = hook.onGetCache(key, getCacheService(), val[0]);
        }, ICacheServiceHook.OPERATE_FLAG_GET);
        return val[0];
    }

    public boolean exists(String key) {
        foreachHook((hook) -> {
            hook.onTestCache(key, getCacheService());
        }, ICacheServiceHook.OPERATE_FLAG_TEST);
        return getCacheService().exists(key);
    }

    public void remove(String key) {

        foreachHook((hook) -> {
            hook.onRemoveCache(key, getCacheService());
        }, ICacheServiceHook.OPERATE_FLAG_REMOVE);

        getCacheService().remove(key);
    }

    public void clear() {
        getCacheService().clear();
        foreachHook((hook) -> {
            hook.onClearCache(getCacheService());
        }, ICacheServiceHook.OPERATE_FLAG_CLEAR);
    }

    public long size() {
        return getCacheService().cacheSize();
    }

}
