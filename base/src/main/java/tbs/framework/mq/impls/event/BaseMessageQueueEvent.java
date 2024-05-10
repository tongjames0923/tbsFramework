package tbs.framework.mq.impls.event;

import cn.hutool.core.collection.CollUtil;
import tbs.framework.mq.IMessage;
import tbs.framework.mq.IMessageConsumer;
import tbs.framework.mq.IMessageConsumerManager;
import tbs.framework.mq.IMessageQueueEvents;

import java.util.List;

/**
 * @author abstergo
 */
public abstract class BaseMessageQueueEvent implements IMessageQueueEvents {

    protected abstract IMessageConsumerManager getConsumerManager();

    @Override
    public boolean onMessageReceived(IMessage message) {
        List<IMessageConsumer> consumers = getConsumerManager().selectMessageConsumer(message);
        if (CollUtil.isEmpty(consumers)) {
            throw new UnsupportedOperationException("none consumer found");
        }
        for (IMessageConsumer consumer : consumers) {
            int r = 0;
            try {
                if (!consumer.consume(message)) {
                    throw new RuntimeException("consumer not consumed");
                }
            } catch (Exception e) {
                if (!onMessageFailed(message, r++, MessageHandleType.Consume, e, consumer)) {
                    break;
                }
            }
        }
        return true;
    }
}
