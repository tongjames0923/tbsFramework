package tbs.framework.redis.impls.cache

import tbs.framework.cache.ICacheService
import tbs.framework.cache.IExpireable
import tbs.framework.cache.managers.AbstractCacheManager
import tbs.framework.redis.impls.cache.services.RedisCacheServiceImpl
import tbs.framework.utils.BeanUtil
import java.time.Duration
import java.util.concurrent.TimeUnit

class RedisExpiredImpl() : IExpireable {
    fun getService(cacheService: ICacheService): RedisCacheServiceImpl {
        return BeanUtil.getAs(cacheService)
    }

    override fun expire(
        key: String,
        duration: Duration,
        manager: AbstractCacheManager,
        cs: ICacheService
    ) {
        var cacheService = getService(cs)
        cacheService.redisTemplate.expire(cacheService.mixKey(key), duration)
    }

    override fun remaining(
        key: String,
        manager: AbstractCacheManager,
        cs: ICacheService
    ): Long {
        var cacheService = getService(cs)
        return cacheService.redisTemplate.getExpire(cacheService.mixKey(key), TimeUnit.SECONDS)
    }

    override fun execute() {
    }
}