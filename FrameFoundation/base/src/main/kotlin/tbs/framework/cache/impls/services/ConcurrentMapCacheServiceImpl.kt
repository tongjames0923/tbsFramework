package tbs.framework.cache.impls.services

import tbs.framework.cache.ICacheService
import java.util.concurrent.ConcurrentHashMap

class ConcurrentMapCacheServiceImpl : ICacheService {
    private val map = ConcurrentHashMap<String, Any>()

    override fun put(key: String?, value: Any?, override: Boolean) {
        if (key == null || value == null) {
            throw RuntimeException("cannot put null or empty");
        }
        if (override) {
            map.put(key, value);
        } else {
            if (exists(key)) {
                return
            }
            map.put(key, value)
        }
    }

    override fun get(key: String?): Any? {
        if (key == null) {
            throw RuntimeException("cannot get null or empty");
        }
        return map[key]
    }

    override fun exists(key: String?): Boolean {
        return map.containsKey(key);
    }

    override fun remove(key: String?) {
        if (key == null) {
            throw RuntimeException("cannot remove null or empty");
        }
        map.remove(key)
    }

    override fun clear() {
        map.clear();
    }

    override fun cacheSize(): Long {
        return map.size.toLong()
    }
}