package tbs.framework.mq;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author abstergo
 */
public abstract class AbstractMessageCenter implements InitializingBean, DisposableBean {

    private AtomicBoolean started = new AtomicBoolean(false);

    public static void checkInputConsumer(IMessageConsumer messageConsumer) {
        if (messageConsumer == null || StrUtil.isEmpty(messageConsumer.consumerId())) {
            throw new NullPointerException("消费者为空或消息者id为空");
        }
        if (CollUtil.isEmpty(messageConsumer.avaliableTopics())) {
            throw new NoSuchElementException("none topic to listen");
        }
    }

    protected abstract Optional<IMessageQueueEvents> getMessageQueueEvents();

    protected abstract Optional<IMessageConsumerManager> getMessageConsumerManager();

    protected void setStarted(boolean started) {
        this.started.set(started);
    }

    public boolean isStart() {
        return started.get();
    }

    public AbstractMessageCenter appendConsumer(IMessageConsumer messageConsumer) {
        getMessageConsumerManager().orElseThrow(() -> {
            return new UnsupportedOperationException("none consumer manager");
        }).setMessageConsumer(messageConsumer);
        return this;
    }

    public AbstractMessageCenter remove(IMessageConsumer consumer) {
        getMessageConsumerManager().orElseThrow(() -> {
            return new UnsupportedOperationException("none consumer manager");
        }).removeMessageConsumer(consumer);
        return this;
    }

    public boolean messageArrived(IMessage msg) {
        return getMessageQueueEvents().orElseThrow(() -> {
            return new UnsupportedOperationException("none events avaliable");
        }).onMessageReceived(msg);
    }

    public void messageSent(IMessage msg) {
        getMessageQueueEvents().orElseThrow(() -> {
            return new UnsupportedOperationException("none events avaliable");
        }).onMessageSent(msg);
    }

    public boolean handleMessageError(IMessage msg, int r, IMessageQueueEvents.MessageHandleType type, Throwable e,
        IMessageConsumer consumer) {
        return getMessageQueueEvents().orElseThrow(() -> {
            return new UnsupportedOperationException("none events avaliable");
        }).onMessageFailed(msg, r, type, e, consumer);
    }

    public boolean errorOnConsume(IMessage m, int r, Throwable e, IMessageConsumer c) {
        return handleMessageError(m, r, IMessageQueueEvents.MessageHandleType.Consume, e, c);
    }

    public boolean errorOnRecive(int r, Throwable e) {
        return handleMessageError(null, r, IMessageQueueEvents.MessageHandleType.Receive, e, null);
    }

    public boolean errorOnSend(IMessage m, int r, Throwable e) {
        return handleMessageError(m, r, IMessageQueueEvents.MessageHandleType.Send, e, null);
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
        AtomicInteger tryTimes = new AtomicInteger();
        while (true) {
            try {
                sendMessage(message);
                break;
            } catch (Exception e) {
                if (getMessageQueueEvents().map((ev) -> {
                    return !ev.onMessageFailed(message, tryTimes.getAndIncrement(),
                        IMessageQueueEvents.MessageHandleType.Send, e, null);
                }).orElse(true)) {
                    break;
                }
            }
        }
        getMessageQueueEvents().ifPresent((ev) -> {
            ev.onMessageSent(message);
        });
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
    protected abstract void centerStartToWork();

    /**
     * 消息中心停止
     */
    protected abstract void centerStopToWork();

    @Override
    public void afterPropertiesSet() throws Exception {
        setStarted(true);
        centerStartToWork();
    }

    @Override
    public void destroy() throws Exception {
        setStarted(false);
        centerStopToWork();
    }
}
