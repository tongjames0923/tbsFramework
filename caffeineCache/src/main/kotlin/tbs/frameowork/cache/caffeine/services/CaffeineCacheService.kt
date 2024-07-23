package tbs.frameowork.cache.caffeine.services

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import tbs.framework.cache.ICacheService
import java.time.Duration

/**
 * CaffeineCacheService 类实现了 ICacheService 接口，提供基于 Caffeine 缓存的缓存服务。
 *
 * @param max 缓存的最大容量
 * @param afterRead 缓存项在最后一次被访问后的过期时间
 * @param afterWrite 缓存项在创建后的过期时间
 */
class CaffeineCacheService :
    ICacheService {
    /**
     * 用于存储缓存数据的 Cache 实例。
     */
    val source: Cache<String, Any>;

    /**
     * 构造函数用于初始化 CaffeineCacheService 实例。
     *
     * @param max 缓存的最大容量
     * @param afterRead 缓存项在最后一次被访问后的过期时间
     * @param afterWrite 缓存项在创建后的过期时间
     */
    constructor(max: Long = 1024L, afterRead: Duration? = null, afterWrite: Duration? = null) {
        val caffeine = Caffeine.newBuilder().maximumSize(max)
        afterRead?.let { caffeine.expireAfterAccess(it) }
        afterWrite?.let { caffeine.expireAfterWrite(it) }
        source = caffeine.build()
    }

    /**
     * 将键值对存入缓存。如果键已经存在且 override 为 false，则不会覆盖现有项。
     *
     * @param key 缓存项的键
     * @param value 缓存项的值
     * @param override 是否覆盖现有项
     */
    override fun put(key: String, value: Any, override: Boolean) {
        if (override || !exists(key)) {
            source.put(key, value)
        }
    }

    /**
     * 从缓存中获取指定键的值。
     *
     * @param key 缓存项的键
     * @return 缓存项的值
     */
    override fun get(key: String): Any {
        return source.getIfPresent(key)!!
    }

    /**
     * 检查缓存中是否存在指定键的项。
     *
     * @param key 缓存项的键
     * @return 是否存在指定键的项
     */
    override fun exists(key: String): Boolean {
        return source.asMap().containsKey(key)
    }

    /**
     * 从缓存中移除指定键的项。
     *
     * @param key 缓存项的键
     */
    override fun remove(key: String) {
        source.invalidate(key)
    }

    /**
     * 清空缓存中的所有项。
     */
    override fun clear() {
        source.invalidateAll()
    }

    /**
     * 获取缓存中的项数。
     *
     * @return 缓存中的项数
     */
    override fun cacheSize(): Long {
        return source.estimatedSize()
    }
}
