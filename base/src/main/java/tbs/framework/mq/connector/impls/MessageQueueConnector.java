package tbs.framework.mq.connector.impls;

import tbs.framework.mq.connector.IMessageConnector;
import tbs.framework.mq.message.IMessage;
import tbs.framework.mq.receiver.IMessageReceiver;
import tbs.framework.mq.receiver.impls.QueueReceiver;

import java.util.List;
import java.util.PriorityQueue;

/**
 * @author Abstergo
 */
public class MessageQueueConnector implements IMessageConnector {

    private PriorityQueue<IMessage> messageQueue = new PriorityQueue<>(MessageQueueConnector::compare);

    private static int compare(IMessage m1, IMessage m2) {
        return m1.getPriority() > m2.getPriority() ? -1 : 1;
    }

    @Override
    public void factoryMessageReceivers(List<IMessageReceiver> receivers) {
        receivers.forEach((p) -> {
            if (p instanceof QueueReceiver) {
                QueueReceiver queueReceiver = (QueueReceiver)p;
                queueReceiver.setQueue(messageQueue);
            }
        });
    }
}
