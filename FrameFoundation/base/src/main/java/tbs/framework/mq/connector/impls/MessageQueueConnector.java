package tbs.framework.mq.connector.impls;

import tbs.framework.mq.center.AbstractMessageCenter;
import tbs.framework.mq.connector.IMessageConnector;
import tbs.framework.mq.message.IMessage;
import tbs.framework.mq.receiver.IMessageReceiver;
import tbs.framework.mq.receiver.impls.QueueReceiver;
import tbs.framework.mq.sender.IMessagePublisher;
import tbs.framework.mq.sender.impls.MessageQueueSender;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;

/**
 * @author Abstergo
 */
public class MessageQueueConnector implements IMessageConnector {

    private PriorityQueue<IMessage> messageQueue = new PriorityQueue<>(MessageQueueConnector::compare);

    @Resource
    List<QueueReceiver> queueReceivers;

    @Resource
    MessageQueueSender sender;

    private static int compare(IMessage m1, IMessage m2) {
        return m1.getPriority() > m2.getPriority() ? -1 : 1;
    }

    @Override
    public void createPublishers(AbstractMessageCenter center) {
        center.setMessagePublisher(sender);
    }

    @Override
    public void createReceivers(AbstractMessageCenter center) {
        for (QueueReceiver queueReceiver : queueReceivers) {
            queueReceiver.setAvaliable(true);
            queueReceiver.setQueue(messageQueue, this);
            center.addReceivers(queueReceiver);
        }
    }

    @Override
    public void destoryPublishers(AbstractMessageCenter center, IMessagePublisher publishers) {

    }

    @Override
    public void destoryReceivers(AbstractMessageCenter center, List<IMessageReceiver> receivers) {
        for (QueueReceiver queueReceiver : queueReceivers) {
            for (IMessageReceiver receiver : receivers) {
                if (Objects.equals(queueReceiver, receivers)) {
                    queueReceiver.setAvaliable(false);
                    queueReceiver.setQueue(null, this);
                }
            }
        }
    }
}
