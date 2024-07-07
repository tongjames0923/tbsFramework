package tbs.framework.cache.hooks;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.Ordered;
import tbs.framework.cache.managers.AbstractCacheManager;

/**
 * @author Abstergo
 */
public interface ICacheServiceHook extends Ordered {

    public static final int OPERATE_FLAG_SET = 0x1, OPERATE_FLAG_GET = 0x2, OPERATE_FLAG_REMOVE = 0x3,
        OPERATE_FLAG_CLEAR = 0x4, OPERATE_FLAG_TEST = 0x5;

    /**
     * 设置缓存时触发 {@link AbstractCacheManager#put(String, Object, boolean)}
     *
     * @param key      键
     * @param value    存入的值
     * @param override 是否覆盖标志
     * @param host     缓存服务
     */
    void onSetCache(@NotNull String key, Object value, boolean override, @NotNull AbstractCacheManager host);

    /**
     * 获取缓存时触发 {@link AbstractCacheManager#get(String)}
     *
     * @param key          键
     * @param cacheService 缓存服务
     * @param value
     */
    Object onGetCache(@NotNull String key, @NotNull AbstractCacheManager cacheService, Object value);

    /**
     * 移除缓存时触发 {@link AbstractCacheManager#remove(String)}
     *
     * @param key          键
     * @param cacheService 缓存服务
     */
    void onRemoveCache(@NotNull String key, @NotNull AbstractCacheManager cacheService);

    /**
     * 清空缓存的钩子 调用{@link  AbstractCacheManager#clear()}触发
     *
     * @param cacheService 缓存服务
     */
    void onClearCache(@NotNull AbstractCacheManager cacheService);

    /**
     * 判断对应键的缓存是否存在 调用{@link AbstractCacheManager#exists(String)}时触发
     *
     * @param cacheService 缓存服务
     * @param key          缓存的的键
     */
    void onTestCache(@NotNull String key, @NotNull AbstractCacheManager cacheService);

    /**
     * @param type 对应操作标志，一般是在Hook接口中
     * @return 钩子是否执行
     */
    default boolean hookAvaliable(int type,@NotNull AbstractCacheManager host) {
        switch (type) {
            case OPERATE_FLAG_CLEAR:
            case OPERATE_FLAG_GET:
            case OPERATE_FLAG_REMOVE:
            case OPERATE_FLAG_SET:
            case OPERATE_FLAG_TEST:
                return true;
            default:
                return false;
        }
    }

    /**
     * @return 执行顺序，升序，默认100
     */
    @Override
    default int getOrder() {
        return 100;
    }

}
