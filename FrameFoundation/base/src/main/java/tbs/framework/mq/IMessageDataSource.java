package tbs.framework.mq;

import tbs.framework.mq.message.IMessage;

public interface IMessageDataSource {
    void addMessage(IMessage message);

    IMessage getMessage();
}
