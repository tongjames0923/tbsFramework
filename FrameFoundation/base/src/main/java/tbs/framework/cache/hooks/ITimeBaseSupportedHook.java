package tbs.framework.cache.hooks;

import tbs.framework.cache.ICacheService;
import tbs.framework.cache.constants.FeatureSupportCode;
import tbs.framework.cache.managers.AbstractCacheManager;

import java.time.Duration;

/**
 * @author Abstergo
 */
public interface ITimeBaseSupportedHook extends ICacheServiceHook {
    public static final int HOOK_OPERATE_FLAG_EXPIRE = 0x6, HOOK_OPERATE_FLAG_REMAIN = 0x7;

    void onSetDelay(String key, Duration delay, ICacheService service);

    void onTimeout(String key, ICacheService service);

    long remainingTime(String key, ICacheService service);

    @Override
    default boolean hookAvaliable(int type, AbstractCacheManager host) {
        if (type == HOOK_OPERATE_FLAG_EXPIRE || type == HOOK_OPERATE_FLAG_REMAIN) {
            return host.featureSupport(FeatureSupportCode.EXPIRED_SUPPORT);
        }
        return ICacheServiceHook.super.hookAvaliable(type, host);
    }
}
