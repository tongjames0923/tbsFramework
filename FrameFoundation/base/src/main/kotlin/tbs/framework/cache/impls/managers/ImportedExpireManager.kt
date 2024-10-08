package tbs.framework.cache.impls.managers

import tbs.framework.cache.ICacheService
import tbs.framework.cache.IExpireable
import tbs.framework.cache.managers.AbstractExpireManager

class ImportedExpireManager : AbstractExpireManager {
    private var timeBaseCacheManager: ICacheService? = null
    private var expiredImpl: IExpireable? = null

    public constructor(service: ICacheService, e: IExpireable) : super() {
        this.timeBaseCacheManager = service
        this.expiredImpl = e
    }
    

    override fun getCacheService(): ICacheService {
        return timeBaseCacheManager!!
    }

    override fun getExpireable(): IExpireable {
        return expiredImpl!!
    }

    override fun toString(): String {
        return "ImportedExpireManager(cacheService=$timeBaseCacheManager, expiredImpl=$expiredImpl)"
    }


}
