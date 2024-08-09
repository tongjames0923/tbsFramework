package tbs.framework.redis.cache.impls

import tbs.framework.cache.ICacheService
import tbs.framework.cache.IExpireable
import tbs.framework.cache.impls.LocalExpiredImpl
import tbs.framework.cache.managers.AbstractCacheManager
import tbs.framework.redis.IRedisTemplateSupport
import java.time.Duration

class HybirdCacheExipireImpl : IExpireable {
    private var localExpired: LocalExpiredImpl;
    private var redisExpired: RedisExpiredImpl;

    constructor(localExpired: LocalExpiredImpl, redisExpired: RedisExpiredImpl) {
        this.localExpired = localExpired
        this.redisExpired = redisExpired
    }


    private fun expireByType(
        key: String, duration: Duration,
        manager: AbstractCacheManager, cacheService: ICacheService
    ) {
        if (cacheService is IRedisTemplateSupport) {
            redisExpired.expire(key, duration, manager, cacheService)
        } else {
            localExpired.expire(key, duration, manager, cacheService)
        }
    }

    private fun remainByType(
        key: String, manager: AbstractCacheManager,
        cacheService: ICacheService
    ): Long {
        if (cacheService is IRedisTemplateSupport) {
            return redisExpired.remaining(key, manager, cacheService)
        } else {
            return localExpired.remaining(key, manager, cacheService)
        }
    }

    public override fun expire(
        key: String, duration: Duration, manager: AbstractCacheManager,
        cacheService: ICacheService
    ) {
        expireByType(key, duration, manager, cacheService)
    }

    public override fun remaining(
        key: String, manager: AbstractCacheManager,
        cacheService: ICacheService
    ): Long {
        return remainByType(key, manager, cacheService)
    }

    public override fun execute() {
        localExpired.execute()
    }
}