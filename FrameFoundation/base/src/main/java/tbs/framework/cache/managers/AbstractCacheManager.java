package tbs.framework.cache.managers;

import tbs.framework.cache.ICacheService;
import tbs.framework.cache.hooks.ICacheServiceHook;

import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author Abstergo
 */
public abstract class AbstractCacheManager<H extends ICacheServiceHook> {
    private Set<H> hooks = new HashSet<>();
    private PriorityQueue<H> queue = new PriorityQueue<>(new Comparator<H>() {
        @Override
        public int compare(H o1, H o2) {
            return o2.getOrder() - o1.getOrder();
        }
    });

    public void addHook(H hook) {
        hooks.add(hook);
        queue.add(hook);

    }

    public void removeHook(H hook) {
        hooks.remove(hook);
        queue.clear();
        for (H h : hooks) {
            queue.add(h);
        }
    }

    public int hookCount() {
        return queue.size();
    }

    protected void foreachHook(Consumer<H> c, int e) {
        for (H hook : queue) {
            if (hook != null && hook.hookAvaliable(e)) {
                c.accept(hook);
            }
        }
    }

    protected abstract ICacheService getCacheService();

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
