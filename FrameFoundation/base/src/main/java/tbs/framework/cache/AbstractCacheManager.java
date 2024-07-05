package tbs.framework.cache;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author Abstergo
 */
public abstract class AbstractCacheManager<H extends ICacheServiceHook> {
    private Set<H> hooks = new HashSet<>();

    public void addHook(H hook) {
        hooks.add(hook);
    }

    public void removeHook(H hook) {
        hooks.remove(hook);
    }

    public int hookCount() {
        return hooks.size();
    }

    protected void foreachHook(Consumer<H> c) {
        for (H hook : hooks) {
            if (hook != null && hook.hookAvaliable()) {
                c.accept(hook);
            }
        }
    }

    protected abstract ICacheService getCacheService();

    public void put(String key, Object value, boolean override) {
        getCacheService().put(key, value, override);
        foreachHook((hook) -> {
            hook.onSetCache(key, value, override, getCacheService());
        });
    }

    public void put(String key, Object value) {
        put(key, value, true);
    }

    public Object get(String key) {
        Object val = getCacheService().get(key);
        foreachHook((hook) -> {
            hook.onGetCache(key, getCacheService());
        });
        return val;
    }

    public boolean exists(String key) {
        foreachHook((hook) -> {
            hook.onTestCache(key, getCacheService());
        });
        return getCacheService().exists(key);
    }

    public void remove(String key) {

        foreachHook((hook) -> {
            hook.onRemoveCache(key, getCacheService());
        });

        getCacheService().remove(key);
    }

    public void clear() {
        getCacheService().clear();
        foreachHook((hook) -> {
            hook.onClearCache(getCacheService());
        });
    }

    public long size() {
        return getCacheService().cacheSize();
    }

}
