package tbs.framework.redis.cache.impls

import tbs.framework.cache.ICacheService
import tbs.framework.cache.IExpireable
import tbs.framework.cache.IkeyMixer
import tbs.framework.cache.managers.AbstractCacheManager
import tbs.framework.redis.IRedisTemplateSupport
import tbs.framework.utils.BeanUtil
import java.time.Duration
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * 基于redisTemplate实现的超时器，需要缓存服务实现 IRedisTemplateSupport
 * @see IRedisTemplateSupport
 *
 */
public class RedisExpiredImpl() : IExpireable {
    fun getService(cacheService: ICacheService): IRedisTemplateSupport {
        return BeanUtil.getAs(cacheService)
    }

    override fun expire(
        key: String,
        duration: Duration,
        manager: AbstractCacheManager,
        cs: ICacheService
    ) {
        var cacheService = getService(cs)
        var key1 = key;
        if (cs is IkeyMixer) {
            key1 = (cs as IkeyMixer).mixKey(key);
        }
        cacheService.redisTemplate.expire(key1, duration)
    }

    override fun remaining(
        key: String,
        manager: AbstractCacheManager,
        cs: ICacheService
    ): Long {

        var cacheService = getService(cs)
        var key1 = key;
        if (cs is IkeyMixer) {
            key1 = (cs as IkeyMixer).mixKey(key);
        }
        return cacheService.redisTemplate.getExpire(key1, TimeUnit.MILLISECONDS)
    }

    override fun execute() {
    }

    override fun isAccept(manager: AbstractCacheManager, cacheService: ICacheService): Optional<IExpireable> {
        when (cacheService) {
            is IRedisTemplateSupport ->
                return Optional.of(this);
            else -> return Optional.empty();
        }
    }
}