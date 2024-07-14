package tbs.framework.mq.sender.impls;

import org.jetbrains.annotations.NotNull;
import tbs.framework.mq.IMessageDataSource;
import tbs.framework.mq.center.AbstractMessageCenter;
import tbs.framework.mq.message.IMessage;
import tbs.framework.mq.sender.IMessagePublisher;

/**
 * 本地消息发布器
 *
 * @author Abstergo
 */
public class LocalProvideSender implements IMessagePublisher {

    private IMessageDataSource messageQueue = null;

    public LocalProvideSender(IMessageDataSource messageQueue) {
        this.messageQueue = messageQueue;
    }

    @Override
    public void publish(IMessage m, @NotNull AbstractMessageCenter center) {
        center.getReceivers().forEach(r -> {
            if (center.getMessageConsumerManager().map((v) -> {
                return v.match(m.getTopic(), r.acceptTopics());
            }).orElse(false)) {
                messageQueue.addMessage(m);
            } else {
                throw new RuntimeException("Topic not match");
            }

        });
    }
}
