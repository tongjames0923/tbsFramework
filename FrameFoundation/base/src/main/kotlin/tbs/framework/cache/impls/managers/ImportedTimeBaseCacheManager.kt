package tbs.framework.cache.impls.managers

import org.springframework.stereotype.Repository
import tbs.framework.cache.AbstractTimeBaseCacheManager
import tbs.framework.cache.ICacheService
import tbs.framework.cache.ITimeBaseSupportedHook

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
