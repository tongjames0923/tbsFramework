package tbs.framework.mq.receiver.impls;

import tbs.framework.lock.annotations.LockIt;
import tbs.framework.lock.impls.SimpleLockAddtionalInfo;
import tbs.framework.mq.connector.IMessageConnector;
import tbs.framework.mq.message.IMessage;
import tbs.framework.proxy.impls.LockProxy;

import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Abstergo
 */
public class QueueReceiver extends AbstractIdentityReceiver {

    private Queue<IMessage> queue;
    AtomicBoolean hasValue = new AtomicBoolean(false);
    IMessageConnector connector;

    public QueueReceiver setQueue(Queue<IMessage> queue, IMessageConnector connector) {
        this.queue = queue;
        this.connector = connector;
        return this;
    }

    SimpleLockAddtionalInfo lockAddtionalInfo = new SimpleLockAddtionalInfo("QUEUE_LOCK");
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
    @LockIt(lockId = "QUEUE_LOCK")
    public void pull(IMessage message) {
        if (!avaliable()) {
            return;
        }
        queue.add(message);
        hasValue.set(true);
    }

}
