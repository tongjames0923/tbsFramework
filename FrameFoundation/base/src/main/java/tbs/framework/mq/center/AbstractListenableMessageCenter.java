package tbs.framework.mq.center;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import org.jetbrains.annotations.Nullable;
import tbs.framework.mq.consumer.IMessageConsumer;
import tbs.framework.mq.message.IMessage;
import tbs.framework.mq.receiver.IMessageReceiver;
import tbs.framework.mq.receiver.impls.AbstractIdentityReceiver;

import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author abstergo
 */
public abstract class AbstractListenableMessageCenter extends AbstractMessageCenter {
    private AtomicBoolean listening = new AtomicBoolean(false);

    /**
     * 检查消息消费器是否合法。
     *
     * @param messageConsumer 消息消费器。
     */
    public static void checkInputConsumer(IMessageConsumer messageConsumer) {
        if (messageConsumer == null || StrUtil.isEmpty(messageConsumer.consumerId())) {
            throw new NullPointerException("消费者为空或消息者id为空");
        }
        if (CollUtil.isEmpty(messageConsumer.avaliableTopics())) {
            throw new NoSuchElementException("none topic to listen");
        }
    }

    /**
     * 检查消息中心是否在监听。
     *
     * @return 是否在监听。
     */
    public boolean isListen() {
        return listening.get();
    }

    /**
     * 启动监听，即启动消息接收器。
     */
    public void listen() {
        listenPreCheck();
        this.listening.set(true);
        while (isStart() && isListen()) {
            for (IMessageReceiver receiver : getReceivers()) {
                if (receiverAvaliable(receiver)) {
                    continue;
                }
                IMessage msg = receiveMsg(receiver);
                if (msg == null) {
                    Thread.yield();
                    continue;
                }

                messageArrived(msg, getConnector(), receiver);

                consumeMessage(msg);
            }
        }
        this.listening.set(false);
    }

    /**
     * 停止监听消息。
     */
    public void stopListen() {
        listening.set(false);
    }

    @Override
    public void destroy() throws Exception {
        stopListen();
        super.destroy();
    }

    private @Nullable IMessage receiveMsg(IMessageReceiver receiver) {
        IMessage msg = null;
        int rr = 0;
        while (true) {
            try {
                msg = receiver.receive();
                break;
            } catch (Exception e) {
                if (!errorOnReceive(e, rr++)) {
                    break;
                }
            }
            tooManyRetryTimes(rr);
        }
        return msg;
    }

    private static boolean receiverAvaliable(IMessageReceiver receiver) {
        if (receiver instanceof AbstractIdentityReceiver) {
            AbstractIdentityReceiver identityReceiver = (AbstractIdentityReceiver)receiver;
            if (!identityReceiver.avaliable()) {
                return true;
            }
        }
        return false;
    }

    private void listenPreCheck() {
        if (!isStart()) {
            throw new IllegalStateException("message center is not started");
        }
        if (isListen()) {
            throw new IllegalStateException("message center is listening");
        }
    }
}
