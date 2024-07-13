package tbs.framework.redis.impls.mq.receiver;

import org.jetbrains.annotations.NotNull;
import tbs.framework.base.utils.LogFactory;
import tbs.framework.lock.expections.ObtainLockFailException;
import tbs.framework.log.ILogger;
import tbs.framework.mq.center.AbstractMessageCenter;
import tbs.framework.mq.connector.IMessageConnector;
import tbs.framework.mq.consumer.IMessageConsumer;
import tbs.framework.mq.message.IMessage;
import tbs.framework.mq.receiver.impls.AbstractIdentityReceiver;
import tbs.framework.redis.impls.lock.RedisTaskBlockLock;
import tbs.framework.redis.properties.RedisMqProperty;

import java.util.Set;

/**
 * @author Abstergo
 */
public class RedisChannelReceiver extends AbstractIdentityReceiver {
    private ILogger logger = null;

    private AbstractMessageCenter center;

    private IMessageConsumer consumer;

    private IMessageConsumer getConsumer() {
        return consumer;
    }

    RedisMqProperty mqProperty;

    private RedisTaskBlockLock taksBlockLock;

    private IMessageConnector builder;

    public RedisChannelReceiver(AbstractMessageCenter center, IMessageConsumer consumer, RedisMqProperty mqProperty,
        RedisTaskBlockLock taksBlockLock, IMessageConnector builder) {
        this.center = center;
        this.consumer = consumer;
        this.mqProperty = mqProperty;
        this.taksBlockLock = taksBlockLock;
        this.builder = builder;
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
            logger = LogFactory.Companion.getInstance().getLogger(this.getClass().getName());
        }
        return logger;
    }
}
