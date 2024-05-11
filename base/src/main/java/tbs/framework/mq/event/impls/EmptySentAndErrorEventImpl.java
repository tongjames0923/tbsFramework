package tbs.framework.mq.event.impls;

import tbs.framework.mq.consumer.IMessageConsumer;
import tbs.framework.mq.consumer.manager.IMessageConsumerManager;
import tbs.framework.mq.message.IMessage;

import javax.annotation.Resource;

/**
 * @author abstergo
 */
public class EmptySentAndErrorEventImpl extends BaseMessageQueueEvent {
    @Resource
    private IMessageConsumerManager messageConsumerManager;

    @Override
    public void onMessageSent(IMessage message) {
    }

    @Override
    public boolean onMessageFailed(IMessage message, int retryed, MessageHandleType type, Throwable throwable,
        IMessageConsumer consumer) {
        return false;
    }

    @Override
    protected IMessageConsumerManager getConsumerManager() {
        return messageConsumerManager;
    }
}
