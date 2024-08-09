package tbs.framework.cache.impls

import org.slf4j.LoggerFactory
import tbs.framework.cache.ICacheService
import tbs.framework.cache.IExpireable
import tbs.framework.cache.managers.AbstractCacheManager
import tbs.framework.log.ILogger
import tbs.framework.log.annotations.AutoLogger
import java.time.Duration
import java.util.concurrent.DelayQueue
import java.util.concurrent.Delayed
import java.util.concurrent.TimeUnit

/**
 * 通用型超时器，运行execute以清理过期数据，请在合适的时候调用execute()
 *
 */
class LocalExpiredImpl : IExpireable {

    @AutoLogger
    private lateinit var log: ILogger;

    class CacheEntry(
        var key: String,
        var expiration: Long,
        val service: AbstractCacheManager,
        val cacheService: ICacheService
    ) : Delayed {
        public override fun getDelay(unit: TimeUnit): Long {
            return unit.convert(
                Duration.ofMillis(expiration).toMillis() - System.currentTimeMillis(), TimeUnit.MILLISECONDS
            )
        }

        public override fun compareTo(other: Delayed): Int {
            val diff = getDelay(TimeUnit.MILLISECONDS) - other.getDelay(TimeUnit.MILLISECONDS)
            return java.lang.Long.compare(diff, 0)
        }

        override fun toString(): String {
            return "CacheEntry(key='$key', expiration=$expiration, manager=$service, cacheService=$cacheService)"
        }

    }

    val queue = DelayQueue<CacheEntry>()
    val map = HashMap<String, CacheEntry>();


    private fun cleanQueue() {
        var cnt = 0L;
        synchronized(this)
        {
            while (queue.isNotEmpty()) {
                val item = queue.poll();
                if (item != null) {
                    val delay: Long = item.getDelay(TimeUnit.MILLISECONDS)
                    if (delay > 0) {
                        queue.add(item)
                        break
                    } else {
                        cnt++;
                        item.service.remove(item.key)
                        map.remove(item.key)
                    }
                } else {
                    break
                }
            }
        }
        log.trace("clean expired cache cnt:{}", cnt)
    }

    override fun expire(
        key: String,
        duration: Duration,
        manager: AbstractCacheManager,
        cacheService: ICacheService
    ) {
        synchronized(this)
        {
            val now = System.currentTimeMillis()
            val e = CacheEntry(key, now + duration.toMillis(), manager, cacheService)
            log.trace("new cache Object:{}", e)
            queue.add(e);
            map[key] = e
        }

    }

    override fun remaining(
        key: String,
        manager: AbstractCacheManager,
        cacheService: ICacheService
    ): Long {
        var now = -1L;
        synchronized(key)
        {
            now = map.getOrDefault(key, null)?.getDelay(TimeUnit.MILLISECONDS) ?: -1
        }
        return now;
    }

    override fun execute() {
        cleanQueue();
    }
}