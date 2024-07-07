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
 * The type Abstract cache manager.
 *
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

    /**
     * Add hook abstract cache manager.
     *
     * @param hook the hook
     * @return the abstract cache manager
     */
    public AbstractCacheManager addHook(ICacheServiceHook hook) {
        if (!hookSupport(hook)) {
            throw new UnsupportedOperationException("this hook can not be supported");
        }
        hooks.add(hook);
        queue.add(hook);
        return this;
    }

    /**
     * Remove hook abstract cache manager.
     *
     * @param hook the hook
     * @return the abstract cache manager
     */
    public AbstractCacheManager removeHook(ICacheServiceHook hook) {
        hooks.remove(hook);
        queue.clear();
        for (ICacheServiceHook h : hooks) {
            queue.add(h);
        }
        return this;
    }

    /**
     * Hook support boolean.
     *
     * @param hook the hook
     * @return the boolean
     */
    public boolean hookSupport(@NotNull ICacheServiceHook hook) {
        return true;
    }

    /**
     * 支持的除基础功能代码，内置功能支持见{@link tbs.framework.cache.constants.FeatureSupportCode}
     *
     * @param code the code
     * @return the boolean
     */
    public boolean featureSupport(int code) {
        return false;
    }

    /**
     * Hook count int.
     *
     * @return the int
     */
    public int hookCount() {
        return queue.size();
    }

    /**
     * Foreach hook.
     *
     * @param c the c
     * @param e the e
     */
    protected void foreachHook(Consumer<ICacheServiceHook> c, int e) {
        for (ICacheServiceHook hook : queue) {
            synchronized (queue) {
                if (hook != null && hook.hookAvaliable(e, this)) {
                    c.accept(hook);
                }
            }
        }
    }

    /**
     * Put.
     *
     * @param key      the key
     * @param value    the value
     * @param override 若存在是否覆盖
     */
    public void put(String key, Object value, boolean override) {
        hookForPut(key, value, override);
        getCacheService().put(key, value, override);
    }

    /**
     * Hook for put. {@link AbstractCacheManager#put(String, Object, boolean)}
     *
     * @param key      the key
     * @param value    the value
     * @param override the override
     */
    protected void hookForPut(String key, Object value, boolean override) {
        foreachHook((hook) -> {
            hook.onSetCache(key, value, override, this);
        }, ICacheServiceHook.OPERATE_FLAG_SET);
    }

    /**
     * Put. 默认覆盖
     *
     * @param key   the key
     * @param value the value
     */
    public void put(String key, Object value) {
        put(key, value, true);
    }

    /**
     * Get object.
     *
     * @param key the key
     * @return the object
     */
    public Object get(String key) {
        hookForGet(key);
        return getCacheService().get(key);
    }

    /**
     * Hook for get. 获取数据时钩子
     *
     * @param key the key
     */
    protected void hookForGet(String key) {
        foreachHook((hook) -> {
            hook.onGetCache(key, this);
        }, ICacheServiceHook.OPERATE_FLAG_GET);
    }

    /**
     * Exists boolean.
     *
     * @param key the key
     * @return the boolean
     */
    public boolean exists(String key) {
        hookForExist(key);
        return getCacheService().exists(key);
    }

    /**
     * Hook for exist. 测试是否存在时钩子运行实现
     *
     * @param key the key
     */
    protected void hookForExist(String key) {
        foreachHook((hook) -> {
            hook.onTestCache(key, this);
        }, ICacheServiceHook.OPERATE_FLAG_TEST);
    }

    /**
     * Remove.
     *
     * @param key the key
     */
    public void remove(String key) {

        hookForRemove(key);

        getCacheService().remove(key);
    }

    /**
     * Hook for remove.
     *
     * @param key the key
     */
    protected void hookForRemove(String key) {
        foreachHook((hook) -> {
            hook.onRemoveCache(key, this);
        }, ICacheServiceHook.OPERATE_FLAG_REMOVE);
    }

    /**
     * Clear.
     */
    public void clear() {
        hookForClear();
        getCacheService().clear();
    }

    /**
     * Hook for clear.
     */
    protected void hookForClear() {
        foreachHook((hook) -> {
            hook.onClearCache(this);
        }, ICacheServiceHook.OPERATE_FLAG_CLEAR);
    }

    /**
     * Size long.
     *
     * @return the long
     */
    public long size() {
        return getCacheService().cacheSize();
    }

}
