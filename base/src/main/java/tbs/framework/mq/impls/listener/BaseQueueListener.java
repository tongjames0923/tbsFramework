package tbs.framework.mq.impls.listener;

import tbs.framework.mq.*;

/**
 * @author abstergo
 */
public abstract class BaseQueueListener implements IMessageListener {

    private static final String LOCK_NAME = "SIMPLE_MSG_CENTER_LOCK";

    /**
     * 获取消息队列
     *
     * @return
     */
    public abstract IMessageQueue getQueue();

    IMessageReceiver receiver = new IMessageReceiver() {
        @Override
        public IMessage receive() {
            return getQueue().getNext();
        }
    };

    @Override
    public IMessageReceiver getMessageReceiver() {
        return receiver;
    }

    @Override
    public void listen(AbstractMessageCenter messageCenter) {
        IMessage message = null;
        int r = 0;
        while (true) {
            try {
                message = getMessageReceiver().receive();
                break;
            } catch (Exception e) {
                message = null;
                if (!messageCenter.errorOnRecive(r++, e)) {
                    break;
                }
            }
        }
        if (message == null) {
            Thread.yield();
            return;
        }
        messageCenter.messageArrived(message);
        messageCenter.consumeMessage(message);

    }
}
