package tbs.framework.mq.queue;

import tbs.framework.mq.message.IMessage;

public interface IMessageQueue {
    IMessage getNext();

    boolean isEmpty();

    long size();

    void insert(IMessage message);
}
