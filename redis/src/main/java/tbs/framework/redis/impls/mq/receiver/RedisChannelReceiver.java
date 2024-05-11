package tbs.framework.redis.impls.mq.receiver;

import org.jetbrains.annotations.NotNull;
import tbs.framework.base.lock.expections.ObtainLockFailException;
import tbs.framework.base.log.ILogger;
import tbs.framework.base.utils.LogUtil;
import tbs.framework.mq.connector.IMessageConnector;
import tbs.framework.mq.center.AbstractMessageCenter;
import tbs.framework.mq.consumer.IMessageConsumer;
import tbs.framework.mq.message.IMessage;
import tbs.framework.mq.receiver.IMessageReceiver;
import tbs.framework.redis.impls.lock.RedisTaksBlockLock;

import java.util.Set;

/**
 * @author Abstergo
 */
public class RedisChannelReceiver implements IMessageReceiver {
    private ILogger logger = null;
    private AbstractMessageCenter center;
    private IMessageConsumer bindConsumer;
    private boolean isMessageHandleOnce = false;
    private RedisTaksBlockLock taksBlockLock;
    private IMessageConnector builder;

    public RedisChannelReceiver(AbstractMessageCenter center, IMessageConsumer bindConsumer,
        boolean isMessageHandleOnce, RedisTaksBlockLock taksBlockLock, IMessageConnector builder) {
        this.center = center;
        this.bindConsumer = bindConsumer;
        this.isMessageHandleOnce = isMessageHandleOnce;
        this.taksBlockLock = taksBlockLock;
        this.builder = builder;
    }

    public Set<String> channelSet() {
        return bindConsumer.avaliableTopics();
    }

    @Override
    @Deprecated
    public IMessage receive() {
        return null;
    }

    @Override
    public Set<String> acceptTopics() {
        return bindConsumer.avaliableTopics();
    }

    @Override
    public void pull(IMessage message) {
        center.messageArrived(message, builder, this);
        try {
            if (isMessageHandleOnce) {
                taksBlockLock.lock(lockId(message));
            }
            center.consumeMessages(bindConsumer, message);
        } catch (ObtainLockFailException r) {
            getLogger().warn("get lock to fail:{}", r.getMessage());
        } finally {
            taksBlockLock.unlock(lockId(message));
        }
    }

    @Override
    public void destroy() throws Exception {

    }

    @Override
    public void afterPropertiesSet() throws Exception {
    }

    private static final String LOCK_KEY = "MESSAGE_CENTER_BLOCK_KEY";

    /**
     * @param message 信息
     */
    private @NotNull String lockId(IMessage message) {
        return LOCK_KEY + message.getMessageId() + bindConsumer.consumerId();
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
