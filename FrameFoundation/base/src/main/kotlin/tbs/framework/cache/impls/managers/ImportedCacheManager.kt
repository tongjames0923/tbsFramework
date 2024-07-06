package tbs.framework.cache.impls.managers

import tbs.framework.cache.ICacheService
import tbs.framework.cache.hooks.ICacheServiceHook
import tbs.framework.cache.managers.AbstractCacheManager

open class ImportedCacheManager :
    AbstractCacheManager {
    private var service: ICacheService;

    override fun getCacheService(): ICacheService {
        return service
    }

    constructor(service: ICacheService) : super() {
        this.service = service
    }

    override fun hookSupport(hook: ICacheServiceHook): Boolean {
        return true
    }
}

