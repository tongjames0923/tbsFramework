package tbs.framework.cache.impls.hooks

import tbs.framework.cache.ICacheService
import tbs.framework.cache.ITimeBaseSupportedHook
import java.time.Duration
import java.util.concurrent.DelayQueue
import java.util.concurrent.Delayed
import java.util.concurrent.TimeUnit

class TimeBaseEliminateCacheHook : ITimeBaseSupportedHook {
    class CacheEntry(var key: String, var expiration: Long, val service: ICacheService) : Delayed {
        public override fun getDelay(unit: TimeUnit): Long {
            return unit.convert(
                Duration.ofMillis(expiration).toMillis() -
                        System.currentTimeMillis(), TimeUnit.MILLISECONDS
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
                    onTimeout(item.key, item.service)
                }
            } else {
                break
            }
        }


    }

    override fun onSetCache(key: String, value: Any?, override: Boolean, cacheService: ICacheService?) {
        TODO("Not yet implemented")
    }

    override fun onGetCache(
        key: String,
        cacheService: ICacheService?,
        value: Any
    ): Any? {
        cleanQueue()
        return value
    }

    override fun onRemoveCache(key: String, cacheService: ICacheService?) {
    }

    override fun onClearCache(cacheService: ICacheService?) {
        queue.clear();
        map.clear()
    }

    override fun onTestCache(key: String, cacheService: ICacheService) {
        cleanQueue()
    }

    override fun onSetDelay(key: String?, delay: Duration, service: ICacheService?) {
        val now = System.currentTimeMillis()
        val e = CacheEntry(key!!, now + delay.toMillis(), service!!)
        queue.add(e);
        map.put(key, e)


    }


    override fun onTimeout(key: String?, service: ICacheService?) {
        service!!.remove(key);
        map.remove(key)
    }

    override fun remainingTime(key: String?, service: ICacheService?): Long {
        return map.getOrDefault(key, null)?.getDelay(TimeUnit.MILLISECONDS) ?: -1;
    }

}