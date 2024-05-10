package tbs.framework.mq.impls.event;

import tbs.framework.mq.IMessage;
import tbs.framework.mq.IMessageConsumer;
import tbs.framework.mq.IMessageConsumerManager;

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
