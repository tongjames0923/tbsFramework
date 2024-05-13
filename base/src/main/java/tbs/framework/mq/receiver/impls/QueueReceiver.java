package tbs.framework.mq.receiver.impls;

import tbs.framework.base.lock.annotations.LockIt;
import tbs.framework.base.lock.impls.SimpleLockAddtionalInfo;
import tbs.framework.base.proxy.impls.LockProxy;
import tbs.framework.mq.message.IMessage;
import tbs.framework.mq.receiver.IMessageReceiver;

import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Abstergo
 */
public class QueueReceiver implements IMessageReceiver {

    private Queue<IMessage> queue;
    AtomicBoolean hasValue = new AtomicBoolean(false);


    public QueueReceiver setQueue(Queue<IMessage> queue) {
        this.queue = queue;
        return this;
    }

    SimpleLockAddtionalInfo lockAddtionalInfo = new SimpleLockAddtionalInfo("QUEUE_LOCK");
    @Override
    public IMessage receive() {
        while (!hasValue.get()) {
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
    @LockIt(lockId = "QUEUE_LOCK")
    public void pull(IMessage message) {
        queue.add(message);
        hasValue.set(true);
    }

}
