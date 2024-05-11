package tbs.framework.mq.receiver.impls;

import tbs.framework.base.lock.annotations.LockIt;
import tbs.framework.mq.message.IMessage;
import tbs.framework.mq.receiver.IMessageReceiver;

import java.util.Queue;

/**
 * @author Abstergo
 */
public class QueueReceiver implements IMessageReceiver {

    private Queue<IMessage> queue;

    public QueueReceiver setQueue(Queue<IMessage> queue) {
        this.queue = queue;
        return this;
    }

    @Override
    @LockIt(lockId = "QUEUE_LOCK")
    public IMessage receive() {
        return queue.poll();
    }

    @Override
    @LockIt(lockId = "QUEUE_LOCK")
    public void pull(IMessage message) {
        queue.add(message);
    }

    @Override
    public void destroy() throws Exception {

    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
