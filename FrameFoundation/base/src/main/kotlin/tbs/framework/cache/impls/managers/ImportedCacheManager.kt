package tbs.framework.cache.impls.managers

import tbs.framework.cache.managers.AbstractCacheManager
import tbs.framework.cache.ICacheService
import tbs.framework.cache.hooks.ICacheServiceHook

open class ImportedCacheManager<H : ICacheServiceHook> :
    AbstractCacheManager<H> {
    private var service: ICacheService;

    override fun getCacheService(): ICacheService {
        return service
    }

    constructor(service: ICacheService) : super() {
        this.service = service
    }
}

