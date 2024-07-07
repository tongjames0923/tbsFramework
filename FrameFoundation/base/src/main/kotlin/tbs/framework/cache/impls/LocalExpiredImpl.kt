package tbs.framework.cache.impls

import tbs.framework.cache.ICacheService
import tbs.framework.cache.IExpireable
import tbs.framework.cache.managers.AbstractCacheManager
import java.time.Duration
import java.util.concurrent.DelayQueue
import java.util.concurrent.Delayed
import java.util.concurrent.TimeUnit

class LocalExpiredImpl : IExpireable {

    class CacheEntry(var key: String, var expiration: Long, val service: ICacheService) : Delayed {
        public override fun getDelay(unit: TimeUnit): Long {
            return unit.convert(
                Duration.ofMillis(expiration).toMillis() - System.currentTimeMillis(), TimeUnit.MILLISECONDS
            )
        }

        public override fun compareTo(other: Delayed): Int {
            val diff = getDelay(TimeUnit.MILLISECONDS) - other.getDelay(TimeUnit.MILLISECONDS)
            return java.lang.Long.compare(diff, 0)
        }
    }

    val queue = DelayQueue<CacheEntry>()
    val map = HashMap<String, CacheEntry>();
    private fun cleanQueue() {

        while (queue.isNotEmpty()) {
            val item = queue.poll();
            if (item != null) {
                val delay: Long = item.getDelay(TimeUnit.MILLISECONDS)
                if (delay > 0) {
                    queue.add(item)
                    break
                } else {
                    map.remove(item.key)
                }
            } else {
                break
            }
        }


    }

    override fun expire(
        key: String,
        duration: Duration,
        manager: AbstractCacheManager,
        cacheService: ICacheService
    ) {
        val now = System.currentTimeMillis()
        val e = CacheEntry(key, now + duration.toMillis(), cacheService)
        queue.add(e);
        map[key] = e
    }

    override fun remaining(
        key: String,
        manager: AbstractCacheManager,
        cacheService: ICacheService
    ): Long {
        return map.getOrDefault(key, null)?.getDelay(TimeUnit.MILLISECONDS) ?: -1;
    }

    override fun execute() {
        cleanQueue();
    }
}