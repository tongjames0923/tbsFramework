package tbs.framework.mq.impls

import tbs.framework.cache.IkeyMixer
import tbs.framework.cache.managers.AbstractExpireManager
import tbs.framework.mq.AbstractMessageHandleBlocker
import java.time.Duration
import javax.annotation.Resource


class CacheMessageHandleBlocker : AbstractMessageHandleBlocker, IkeyMixer {
    constructor() : super() {
        val i = 0;
    }

    override fun mixKey(key: String?): String {
        return "MESSAGE_HANDLE_BLOCK:" + super.mixKey(key)
    }


    @Resource
    lateinit var cache: AbstractExpireManager;


    override fun lock(id: String, alive: Duration): Boolean {

        var v = false;
        cache.ifExsist(id, false, true, { k,m ->
            cache.putAndRemove(id, true, false, alive)
            v = true
        });
        return v;
    }

    override fun unlock(id: String, delay: Duration) {
        cache.ifExsist(id, true, true, { k, m ->
            cache.expire(id, delay)
        });
    }
}