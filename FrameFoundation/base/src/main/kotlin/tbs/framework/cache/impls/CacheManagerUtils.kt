package tbs.framework.cache.impls

import tbs.framework.cache.managers.AbstractCacheManager
import tbs.framework.cache.hooks.ICacheServiceHook

public fun <H : ICacheServiceHook, T> AbstractCacheManager<H>.getAs(key: String): T? {

    return this.get(key) as T;
}