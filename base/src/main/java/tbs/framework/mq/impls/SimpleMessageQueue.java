package tbs.framework.mq.impls;

import tbs.framework.mq.IMessage;
import tbs.framework.mq.IMessageQueue;

import java.util.PriorityQueue;

public class SimpleMessageQueue implements IMessageQueue {
    PriorityQueue<IMessage> messages = new PriorityQueue<>(SimpleMessageQueue::compare);

    private static int compare(IMessage o1, IMessage o2) {
        return o1.getPriority() > o2.getPriority() ? 1 : -1;
    }

    @Override
    public IMessage getNext() {
        if (messages.isEmpty()) {
            return null;
        }
        return messages.poll();
    }

    @Override
    public boolean isEmpty() {
        return messages.isEmpty();
    }

    @Override
    public void insert(IMessage message) {
        messages.add(message);
    }
}
