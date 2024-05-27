package tbs.framework.mq.event.impls;

import tbs.framework.log.ILogger;
import tbs.framework.base.utils.LogFactory;
import tbs.framework.mq.connector.IMessageConnector;
import tbs.framework.mq.consumer.IMessageConsumer;
import tbs.framework.mq.event.IMessageQueueEvents;
import tbs.framework.mq.message.IMessage;
import tbs.framework.mq.receiver.IMessageReceiver;

/**
 * @author Abstergo
 */
public class LogMqEvent implements IMessageQueueEvents {

    ILogger logger;

    private ILogger getLogger() {
        if (logger == null) {
            logger = LogFactory.getInstance().getLogger(this.getClass().getName());
        }
        return logger;
    }

    @Override
    public void onMessageReceived(IMessage message, IMessageConnector connector, IMessageReceiver receiver) {
        getLogger().info("message received:{} from {} build by {}", message.toString(), receiver.toString(),
            connector.toString());
    }

    @Override
    public void onMessageSent(IMessage message) {
        getLogger().info("message sent:{}", message.toString());
    }

    @Override
    public boolean onMessageFailed(IMessage message, int retryed, MessageHandleType type, Throwable throwable,
        IMessageConsumer consumer) {
        getLogger().error(throwable, "message error type{} ,retryed{}", type, retryed);
        return false;
    }
}
