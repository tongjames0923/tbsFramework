package tbs.frameowork.cache.caffeine.services

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import tbs.framework.cache.ICacheService
import java.time.Duration

class CaffeineCacheService : ICacheService {
    val source: Cache<String, Any>;

    constructor(max: Long = 1024L, afterRead: Duration? = null, afterWrite: Duration? = null) {
        var caffeine = Caffeine.newBuilder().maximumSize(max)
        if (afterRead != null) {
            caffeine.expireAfterAccess(afterRead)
        }
        if (afterWrite != null) {
            caffeine.expireAfterWrite(afterWrite)
        }
        source = caffeine.build()
    }


    public override fun put(key: String, value: Any, override: Boolean) {
        if (override || !exists(key)) {
            source.put(key, value)
        }
    }

    public override fun get(key: String): Any {
        return source.getIfPresent(key)!!
    }

    public override fun exists(key: String): Boolean {
        return source.asMap().containsKey(key)
    }

    public override fun remove(key: String) {
        source.invalidate(key)
    }

    public override fun clear() {
        source.invalidateAll()
    }

    public override fun cacheSize(): Long {
        return source.estimatedSize()
    }
}