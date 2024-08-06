package tbs.framework.mq.impls

import tbs.framework.cache.IkeyMixer
import tbs.framework.cache.managers.AbstractExpireManager
import tbs.framework.mq.AbstractMessageHandleBlocker
import tbs.framework.mq.message.IMessage
import java.time.Duration
import javax.annotation.Resource


class CacheMessageHandleBlocker : AbstractMessageHandleBlocker, IkeyMixer {
    constructor() : super() {
    }

    override fun mixKey(key: String?): String {
        return "MESSAGE_HANDLE_BLOCK:" + super.mixKey(key)
    }


    @Resource
    lateinit var cache: AbstractExpireManager;


    override fun lock(msg: IMessage, alive: Duration): Boolean {

        var v = false;
        cache.ifExsist(mixKey(msg.messageId), false, true, { k, m ->
            cache.putAndRemove(mixKey(msg.messageId), true, false, alive)
            v = true
        });
        return v;
    }

    override fun unlock(msg: IMessage, delay: Duration) {
        cache.ifExsist(mixKey(msg.messageId), true, true, { k, m ->
            cache.expire(mixKey(msg.messageId), delay)
        });
    }
}