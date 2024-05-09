package tbs.framework.mq;

public interface IMessageQueue {
    IMessage getNext();

    boolean isEmpty();

    void insert(IMessage message);
}
