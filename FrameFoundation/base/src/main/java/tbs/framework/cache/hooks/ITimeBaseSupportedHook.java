package tbs.framework.cache.hooks;

import tbs.framework.cache.ICacheService;
import tbs.framework.cache.hooks.ICacheServiceHook;

import java.time.Duration;

/**
 * @author Abstergo
 */
public interface ITimeBaseSupportedHook extends ICacheServiceHook {
    public static final int HOOK_OPERATE_FLAG_EXPIRE = 0x6, HOOK_OPERATE_FLAG_REMAIN = 0x7;

    void onSetDelay(String key, Duration delay, ICacheService service);

    void onTimeout(String key, ICacheService service);

    long remainingTime(String key, ICacheService service);
}
