package tbs.framework.mq.receiver.impls;

import tbs.framework.lock.annotations.LockIt;
import tbs.framework.lock.impls.SimpleLockAddtionalInfo;
import tbs.framework.mq.connector.IMessageConnector;
import tbs.framework.mq.message.IMessage;
import tbs.framework.proxy.impls.LockProxy;
import tbs.framework.utils.ThreadUtil;

import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The type Queue receiver.
 *
 * @author Abstergo
 */
public class LocalFullFeatureReceiver extends AbstractIdentityReceiver {

    private Queue<IMessage> queue;
    AtomicBoolean hasValue = new AtomicBoolean(false);
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
    public LocalFullFeatureReceiver setQueue(Queue<IMessage> queue, IMessageConnector connector) {
        this.queue = queue;
        this.connector = connector;
        return this;
    }

    private static final String LOCK_NAME = "QUEUE_LOCK";

    /**
     * The Lock addtional info.
     */
    SimpleLockAddtionalInfo lockAddtionalInfo =
        new SimpleLockAddtionalInfo(ThreadUtil.getInstance().getLock(LOCK_NAME));

    @Override
    public IMessage receive() {
        while (!hasValue.get()) {
            if (!avaliable()) {
                return null;
            }
            Thread.yield();
        }
        Optional<IMessage> msg = LockProxy.getInstance().safeProxy((p) -> {
            IMessage message = queue.poll();
            if (queue.isEmpty()) {
                hasValue.set(false);
            }
            return message;
        }, null, lockAddtionalInfo);
        return msg.isPresent() ? msg.get() : null;
    }

    @Override
    public IMessageConnector builder() {
        return connector;
    }

    @Override
    @LockIt(lockId = LOCK_NAME)
    public void pull(IMessage message) {
        if (!avaliable()) {
            return;
        }
        queue.add(message);
        hasValue.set(true);
    }

}
