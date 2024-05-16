package tbs.framework.mq.sender.impls;

import tbs.framework.mq.center.impls.MessageQueueCenter;
import tbs.framework.mq.consumer.manager.IMessageConsumerManager;
import tbs.framework.mq.message.IMessage;
import tbs.framework.mq.sender.IMessagePublisher;

import javax.annotation.Resource;

/**
 * 本地消息发布器
 *
 * @author Abstergo
 */
public class MessageQueueSender implements IMessagePublisher {

    private MessageQueueCenter center;

    public MessageQueueSender(MessageQueueCenter center) {
        this.center = center;
    }

    @Resource
    IMessageConsumerManager consumerManager;

    @Override
    public void publishAll(IMessage... message) {
        center.getReceivers().forEach(r -> {
            for (IMessage m : message) {
                if (consumerManager.match(m.getTopic(), r.acceptTopics())) {
                    r.pull(m);
                }
            }
        });
    }
}
