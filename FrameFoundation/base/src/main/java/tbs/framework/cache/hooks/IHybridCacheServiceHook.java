package tbs.framework.cache.hooks;

import org.jetbrains.annotations.NotNull;
import tbs.framework.cache.ICacheService;
import tbs.framework.cache.constants.FeatureSupportCode;
import tbs.framework.cache.managers.AbstractCacheManager;

public interface IHybridCacheServiceHook extends ICacheServiceHook {
    public static final int HOOK_OPERATE_SWITCH = 0x20, HOOK_OPERATE_ADD_SERVICE = 0x21, HOOK_OPERATE_REMOVE_SERVICE =
        0x22;

    void onSwitch(AbstractCacheManager host, ICacheService oldOne, ICacheService newOne);

    void onNewCacheServiceAdd(AbstractCacheManager host, ICacheService service, long size);

    void onServiceRemove(AbstractCacheManager host, ICacheService service, long size);

    @Override
    default boolean hookAvaliable(int type, @NotNull AbstractCacheManager host) {
        switch (type) {
            case HOOK_OPERATE_SWITCH:
            case HOOK_OPERATE_ADD_SERVICE:
            case HOOK_OPERATE_REMOVE_SERVICE:
                return host.featureSupport(FeatureSupportCode.HYBRID_CACHE_SERVICE);
            default:
                return ICacheServiceHook.super.hookAvaliable(type, host);
        }
    }
}
