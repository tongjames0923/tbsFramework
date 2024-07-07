package tbs.framework.redis.impls.cache.hooks

import tbs.framework.cache.hooks.ICacheServiceHook
import tbs.framework.cache.managers.AbstractCacheManager
import tbs.framework.cache.supports.ICacheServiceHybridSupport
import tbs.framework.utils.BeanUtil

/**
 * 幂指数容量递增，
 * 如i层缓存容量为m^(i),i>=0
 * @param m 幂变量
 */
class HybridCacheExponentiallyIncreasingPutHook(val m: Int = 8) : ICacheServiceHook {
    override fun onSetCache(key: String, value: Any?, override: Boolean, host: AbstractCacheManager) {
        val h: ICacheServiceHybridSupport = BeanUtil.getAs(host);
        var cacheSize: Long = 1;
        val s = h.selectService { c, i ->
            if (i == h.serviceCount() - 1) {
                return@selectService true
            } else {
                cacheSize *= 8;
                if (c.cacheSize() >= cacheSize) {
                    return@selectService false;
                }
                return@selectService true;
            }
        };
        h.setService(s)
    }

    override fun onGetCache(key: String, cacheService: AbstractCacheManager, value: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun onRemoveCache(key: String, cacheService: AbstractCacheManager) {
        TODO("Not yet implemented")
    }

    override fun onClearCache(cacheService: AbstractCacheManager) {
        TODO("Not yet implemented")
    }

    override fun onTestCache(key: String, cacheService: AbstractCacheManager) {
        TODO("Not yet implemented")
    }

    override fun hookAvaliable(type: Int, host: AbstractCacheManager): Boolean {
        return type == ICacheServiceHook.OPERATE_FLAG_SET && host is ICacheServiceHybridSupport;
    }
}