package tbs.framework.mq.impls.queue;

import tbs.framework.base.lock.impls.SimpleLockAddtionalInfo;
import tbs.framework.base.proxy.impls.LockProxy;
import tbs.framework.mq.IMessage;
import tbs.framework.mq.IMessageQueue;

import java.util.Optional;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author abstergo
 */
public class SimpleMessageQueue implements IMessageQueue {
    private PriorityQueue<IMessage> messages = new PriorityQueue<>(SimpleMessageQueue::compare);

    private AtomicLong counter = new AtomicLong(0);

    private String genLockKey() {
        return "MSG_QUEUE_LOCK" + this.toString();
    }
    private static int compare(IMessage o1, IMessage o2) {
        return o1.getPriority() > o2.getPriority() ? 1 : -1;
    }

    @Override
    public IMessage getNext() {
        if (messages.isEmpty()) {
            return null;
        }
        Optional<IMessage> op = LockProxy.getInstance().safeProxy((p) -> {
            IMessage mg = messages.poll();
            counter.set(messages.size());
            return mg;
        }, null, new SimpleLockAddtionalInfo(genLockKey()));
        return op.isEmpty() ? null : op.get();
    }

    @Override
    public boolean isEmpty() {
        return counter.get() == 0L;
    }

    @Override
    public long size() {
        return counter.get();
    }

    @Override
    public void insert(IMessage message) {
        LockProxy.getInstance().quickLock(() -> {
            messages.add(message);
            counter.set(messages.size());
        }, genLockKey());
    }
}
