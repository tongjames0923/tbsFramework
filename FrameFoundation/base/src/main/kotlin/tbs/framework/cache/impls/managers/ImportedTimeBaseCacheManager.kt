package tbs.framework.cache.impls.managers

import tbs.framework.cache.managers.AbstractTimeBaseCacheManager
import tbs.framework.cache.ICacheService
import tbs.framework.cache.hooks.ITimeBaseSupportedHook

class ImportedTimeBaseCacheManager : AbstractTimeBaseCacheManager {
    private var timeBaseCacheManager: ICacheService? = null

    public constructor(service: ICacheService, hook: ITimeBaseSupportedHook) : super() {
        this.timeBaseCacheManager = service
        this.addHook(hook);
    }

    public constructor() : super()


    override fun getCacheService(): ICacheService {
        return timeBaseCacheManager!!
    }
}
