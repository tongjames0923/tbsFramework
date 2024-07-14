package tbs.framework.mq.receiver.impls;

import tbs.framework.mq.IMessageDataSource;
import tbs.framework.mq.connector.IMessageConnector;
import tbs.framework.mq.message.IMessage;

/**
 * The type Queue receiver.
 *
 * @author Abstergo
 */
public class MessageSourceReceiver extends AbstractIdentityReceiver {

    private IMessageDataSource queue;
    /**
     * The Connector.
     */
    IMessageConnector connector;

    public MessageSourceReceiver(IMessageDataSource queue, IMessageConnector connector) {
        this.queue = queue;
        this.connector = connector;
    }

    @Override
    public IMessage receive() {
        if (!avaliable()) {
            throw new RuntimeException("receiver is not avaliable");
        }
        return queue.getMessage();
    }

    @Override
    public IMessageConnector builder() {
        return connector;
    }

}
