package tbs.framework.redis.impls.mq.receiver;

import org.jetbrains.annotations.NotNull;
import tbs.framework.lock.expections.ObtainLockFailException;
import tbs.framework.log.ILogger;
import tbs.framework.mq.center.AbstractMessageCenter;
import tbs.framework.mq.connector.IMessageConnector;
import tbs.framework.mq.consumer.IMessageConsumer;
import tbs.framework.mq.message.IMessage;
import tbs.framework.mq.receiver.impls.AbstractIdentityReceiver;
import tbs.framework.redis.impls.lock.RedisTaksBlockLock;
import tbs.framework.redis.properties.RedisMqProperty;
import tbs.framework.utils.LogUtil;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Abstergo
 */
public class RedisChannelReceiver extends AbstractIdentityReceiver {
    private ILogger logger = null;
    @Resource
    private AbstractMessageCenter center;

    @Resource
    private List<IMessageConsumer> bindConsumers;

    private IMessageConsumer getConsumer() {
        return bindConsumers.get(index);
    }

    private int index = 0;
    private static AtomicInteger aIndex = new AtomicInteger(0);

    @Resource
    RedisMqProperty mqProperty;

    @Resource
    private RedisTaksBlockLock taksBlockLock;
    @Resource
    private IMessageConnector builder;

    public RedisChannelReceiver() {
        this.index = aIndex.getAndIncrement();
    }

    public Set<String> channelSet() {
        return getConsumer().avaliableTopics();
    }

    @Override
    @Deprecated
    public IMessage receive() {
        throw new UnsupportedOperationException(
            "redis channel receiver is not supported for receive(),use pull(IMessage message) instead)");
    }

    @Override
    public IMessageConnector builder() {
        return builder;
    }

    @Override
    public Set<String> acceptTopics() {
        return channelSet();
    }

    @Override
    public void pull(IMessage message) {
        if (!avaliable()) {
            return;
        }
        center.messageArrived(message, builder, this);
        try {
            if (mqProperty.isMessageHandleOnce()) {
                taksBlockLock.lock(lockId(message));
            }
            center.consumeMessages(getConsumer(), message);
        } catch (ObtainLockFailException r) {
            getLogger().warn("get lock to fail:{}", r.getMessage());
        } finally {
            taksBlockLock.unlock(lockId(message));
        }
    }


    private static final String LOCK_KEY = "MESSAGE_CENTER_BLOCK_KEY";

    /**
     * @param message 信息
     */
    private @NotNull String lockId(IMessage message) {
        return LOCK_KEY + message.getMessageId() + getConsumer().consumerId();
    }

    /**
     *
     */
    private ILogger getLogger() {
        if (logger == null) {
            logger = LogUtil.getInstance().getLogger(this.getClass().getName());
        }
        return logger;
    }
}
