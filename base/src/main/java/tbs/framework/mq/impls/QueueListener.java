package tbs.framework.mq.impls;

import tbs.framework.base.proxy.impls.LockProxy;
import tbs.framework.mq.*;

import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author abstergo
 */
public abstract class QueueListener implements IMessageListener {

    private static final String LOCK_NAME = "SIMPLE_MSG_CENTER_LOCK";
    @Resource
    LockProxy lockProxy;

    ExecutorService service = Executors.newFixedThreadPool(1);

    protected abstract IMessageQueue getQueue();

    IMessageReceiver receiver = new IMessageReceiver() {
        @Override
        public IMessage receive() {
            if (getQueue().isEmpty()) {
                return null;
            }
            return getQueue().getNext();
        }
    };

    @Override
    public IMessageReceiver getMessageReceiver() {
        return receiver;
    }

    @Override
    public void listen(AbstractMessageCenter messageCenter) {
        IMessage message = getMessageReceiver().receive();
        if (message == null) {
            Thread.yield();
            return;
        }
        if (!messageCenter.onMessageReceived(message)) {
            getQueue().insert(message);
        }

    }
}
