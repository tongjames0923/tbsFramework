package tbs.framework.mq.receiver.impls;

import tbs.framework.mq.IMessageDataSource;
import tbs.framework.mq.connector.IMessageConnector;
import tbs.framework.mq.message.IMessage;

/**
 * The type Queue receiver.
 *
 * @author Abstergo
 */
public class LocalFullFeatureReceiver extends AbstractIdentityReceiver {

    private IMessageDataSource queue;
    /**
     * The Connector.
     */
    IMessageConnector connector;

    /**
     * 设置关联的队列
     *
     * @param queue     the queue
     * @param connector the connector
     * @return the queue
     */
    public LocalFullFeatureReceiver setQueue(IMessageDataSource queue, IMessageConnector connector) {
        this.queue = queue;
        this.connector = connector;
        return this;
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
