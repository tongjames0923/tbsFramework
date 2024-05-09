package tbs.framework.mq;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;

import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author abstergo
 */
public abstract class AbstractMessageCenter implements IMessageQueueEvents, IMessageConsumerManager {

    private AtomicBoolean started = new AtomicBoolean(false);

    protected void checkInputConsumer(IMessageConsumer messageConsumer) {
        if (messageConsumer == null || StrUtil.isEmpty(messageConsumer.consumerId())) {
            throw new NullPointerException("消费者为空或消息者id为空");
        }
        if (CollUtil.isEmpty(messageConsumer.avaliableTopics())) {
            throw new NoSuchElementException("none topic to listen");
        }
    }

    protected void setStarted(boolean started) {
        this.started.set(started);
    }


    /**
     * 发布消息
     *
     * @param message
     */
    public void publish(IMessage message) {
        if (message == null) {
            throw new NullPointerException("message is null");
        }
        int tryTimes = 0;
        while (true) {
            try {
                sendMessage(message);
                break;
            } catch (Exception e) {
                if (!onMessageFailed(message, tryTimes++, IMessageQueueEvents.MessageHandleType.Send, e, null)) {
                    break;
                }
            }
        }
        onMessageSent(message);
    }

    /**
     * 发送消息
     *
     * @param message
     */
    protected abstract void sendMessage(IMessage message);

    /**
     * 消息中心启动
     */
    public abstract void centerStartToWork();

    /**
     * 消息中心停止
     */
    public abstract void centerStopToWork();

    public boolean isStart() {
        return started.get();
    }

}
