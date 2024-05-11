package tbs.framework.mq.sender.impls;

import tbs.framework.mq.center.impls.MessageQueueCenter;
import tbs.framework.mq.message.IMessage;
import tbs.framework.mq.sender.IMessagePublisher;

/**
 * @author Abstergo
 */
public class MessageQueueSender implements IMessagePublisher {

    private MessageQueueCenter center;

    public MessageQueueSender(MessageQueueCenter center) {
        this.center = center;
    }

    @Override
    public void publishAll(IMessage... message) {
        center.getConnector().map((t) -> {
            t.getReceivers().forEach(r -> {
                for (IMessage m : message) {
                    r.pull(m);
                }
            });
            return t;
        });
    }
}
