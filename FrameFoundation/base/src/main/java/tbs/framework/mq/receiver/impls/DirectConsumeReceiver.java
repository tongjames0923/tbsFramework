package tbs.framework.mq.receiver.impls;

import org.jetbrains.annotations.NotNull;
import tbs.framework.base.properties.MqProperty;
import tbs.framework.base.utils.LogFactory;
import tbs.framework.log.ILogger;
import tbs.framework.mq.AbstractMessageHandleBlocker;
import tbs.framework.mq.center.AbstractMessageCenter;
import tbs.framework.mq.connector.IMessageConnector;
import tbs.framework.mq.message.IMessage;

import java.util.Set;

/**
 * @author Abstergo
 */
public class DirectConsumeReceiver extends AbstractIdentityReceiver {

    private IMessageConnector messageConnector;

    private Set<String> topics;

    private AbstractMessageCenter center;

    private MqProperty property;

    private AbstractMessageHandleBlocker blocker;

    private ILogger logger = null;

    private synchronized ILogger getLogger() {
        if (logger == null) {
            logger = LogFactory.Companion.getInstance().getLogger(this.getClass().getName());
        }
        return logger;
    }

    public DirectConsumeReceiver(IMessageConnector messageConnector, Set<String> topics, AbstractMessageCenter center,
        MqProperty property, AbstractMessageHandleBlocker blocker) {
        this.messageConnector = messageConnector;
        this.topics = topics;
        this.center = center;
        this.blocker = blocker;
        this.property = property;
    }

    public void consumeDirectly(IMessage message) {
        center.messageArrived(message, messageConnector, this);
        try {
            boolean isConsume = true;
            if (blocker != null && property != null) {
                isConsume = blocker.takeLock(lockId(message), property.getTaskBlockAliveTime());
            }
            if (!isConsume) {
                getLogger().debug("message is blocked, message: {}", message);
                return;
            }
            center.consumeMessage(message);
            getLogger().debug("message is consumed, message: {}", message);
        } catch (RuntimeException r) {
            getLogger().error(r, "consumeDirectly error, message: {}", message);
        } finally {
            if (blocker != null && property != null) {
                blocker.unTakeLock(lockId(message), property.getTaskBlockCleanInterval());
            }
        }
    }

    private static final String LOCK_KEY = "MESSAGE_CENTER_BLOCK_KEY";

    /**
     * @param message 信息
     */
    private @NotNull String lockId(IMessage message) {
        return LOCK_KEY + message.getMessageId();
    }

    @Override
    public IMessage receive() {
        throw new RuntimeException("DirectConsumeReceiver not implement");
    }

    @Override
    public IMessageConnector builder() {
        return messageConnector;
    }

    @Override
    public Set<String> acceptTopics() {
        return topics;
    }
}
