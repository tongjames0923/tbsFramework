package tbs.framework.mq;

public interface IMessageQueue {
    IMessage getNext();

    boolean isEmpty();

    long size();

    void insert(IMessage message);
}
