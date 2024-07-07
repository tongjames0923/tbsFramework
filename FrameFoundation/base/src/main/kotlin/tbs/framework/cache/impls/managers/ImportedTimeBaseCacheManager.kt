package tbs.framework.cache.impls.managers

import tbs.framework.cache.ICacheService
import tbs.framework.cache.IExpireable
import tbs.framework.cache.managers.AbstractTimeBaseCacheManager

class ImportedTimeBaseCacheManager : AbstractTimeBaseCacheManager {
    private var timeBaseCacheManager: ICacheService? = null
    private var expiredImpl: IExpireable? = null

    public constructor(service: ICacheService, e: IExpireable) : super() {
        this.timeBaseCacheManager = service
        this.expiredImpl = e
    }

    public constructor() : super()


    override fun getCacheService(): ICacheService {
        return timeBaseCacheManager!!
    }

    override fun getExpireable(): IExpireable {
        return expiredImpl!!
    }

}
