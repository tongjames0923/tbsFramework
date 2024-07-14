package tbs.framework.mq.connector.impls;

import tbs.framework.mq.IMessageDataSource;
import tbs.framework.mq.center.AbstractMessageCenter;
import tbs.framework.mq.connector.IMessageConnector;
import tbs.framework.mq.message.IMessage;
import tbs.framework.mq.receiver.IMessageReceiver;
import tbs.framework.mq.receiver.impls.AbstractIdentityReceiver;
import tbs.framework.mq.receiver.impls.MessageSourceReceiver;
import tbs.framework.mq.sender.IMessagePublisher;
import tbs.framework.mq.sender.impls.LocalProvideSender;

import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * @author Abstergo
 */
public class MessageQueueConnector implements IMessageConnector {

    private IMessageDataSource messageDataSource = new IMessageDataSource() {

        private PriorityBlockingQueue<IMessage> messageQueue =
            new PriorityBlockingQueue<>(32, MessageQueueConnector::compare);

        @Override
        public void addMessage(IMessage message) {
            messageQueue.add(message);
        }

        @Override
        public IMessage getMessage() {
            return messageQueue.poll();
        }
    };

    private static int compare(IMessage m1, IMessage m2) {
        return m1.getPriority() > m2.getPriority() ? -1 : 1;
    }

    @Override
    public void createPublishers(AbstractMessageCenter center) {
        center.setMessagePublisher(new LocalProvideSender(messageDataSource));
    }

    @Override
    public void createReceivers(AbstractMessageCenter center) {
        center.getReceivers().add(new MessageSourceReceiver(messageDataSource, this));
    }

    @Override
    public void destoryPublishers(AbstractMessageCenter center, IMessagePublisher publishers) {

    }

    @Override
    public void destoryReceivers(AbstractMessageCenter center, List<IMessageReceiver> receivers) {

        for (IMessageReceiver receiver : receivers) {
            if (receiver instanceof AbstractIdentityReceiver) {
                ((AbstractIdentityReceiver)receiver).setAvaliable(false);
                center.removeReceivers(receiver);
            }
        }

    }
}
