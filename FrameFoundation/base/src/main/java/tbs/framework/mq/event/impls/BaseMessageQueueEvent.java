package tbs.framework.mq.event.impls;

import cn.hutool.core.collection.CollUtil;
import tbs.framework.mq.connector.IMessageConnector;
import tbs.framework.mq.event.IMessageQueueEvents;
import tbs.framework.mq.message.IMessage;
import tbs.framework.mq.consumer.IMessageConsumer;
import tbs.framework.mq.consumer.manager.IMessageConsumerManager;
import tbs.framework.mq.receiver.IMessageReceiver;

import java.util.List;

/**
 * @author abstergo
 */
public abstract class BaseMessageQueueEvent implements IMessageQueueEvents {

    protected abstract IMessageConsumerManager getConsumerManager();

    @Override
    public void onMessageReceived(IMessage message, IMessageConnector connector, IMessageReceiver receiver) {
        List<IMessageConsumer> consumers = getConsumerManager().selectMessageConsumer(message);
        if (CollUtil.isEmpty(consumers)) {
            throw new UnsupportedOperationException("none consumer found");
        }
    }
}
