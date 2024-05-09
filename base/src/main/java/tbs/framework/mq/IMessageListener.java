package tbs.framework.mq;

public interface IMessageListener {
    IMessageReceiver getMessageReceiver();

    void listen(AbstractMessageCenter messageCenter);

}
