package tbs.framework.cache.impls.managers

import tbs.framework.cache.AbstractCacheManager
import tbs.framework.cache.ICacheService
import tbs.framework.cache.ICacheServiceHook

open class ImportedCacheManager<H : ICacheServiceHook> : AbstractCacheManager<H> {
    private var service: ICacheService;

    override fun getCacheService(): ICacheService {
        return service
    }

    constructor(service: ICacheService) : super() {
        this.service = service
    }
}

