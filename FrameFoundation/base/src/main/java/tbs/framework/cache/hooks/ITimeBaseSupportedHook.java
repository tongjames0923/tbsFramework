package tbs.framework.cache.hooks;

import org.jetbrains.annotations.NotNull;
import tbs.framework.cache.ICacheService;
import tbs.framework.cache.managers.AbstractCacheManager;
import tbs.framework.cache.managers.AbstractTimeBaseCacheManager;
import tbs.framework.cache.supports.ITimeBaseCacheSupport;

import java.time.Duration;

/**
 * @author Abstergo
 */
public interface ITimeBaseSupportedHook extends ICacheServiceHook {
    public static final int HOOK_OPERATE_FLAG_EXPIRE = 0x10, HOOK_OPERATE_FLAG_REMAIN = 0x11;

    void onSetDelay(@NotNull String key, @NotNull Duration delay, @NotNull AbstractTimeBaseCacheManager service);

    void onTimeout(@NotNull String key, @NotNull ICacheService service);

    @Override
    default boolean hookAvaliable(int type, @NotNull AbstractCacheManager host) {
        if (type == HOOK_OPERATE_FLAG_EXPIRE || type == HOOK_OPERATE_FLAG_REMAIN) {
            return host instanceof ITimeBaseCacheSupport;
        }
        return ICacheServiceHook.super.hookAvaliable(type, host);
    }
}
