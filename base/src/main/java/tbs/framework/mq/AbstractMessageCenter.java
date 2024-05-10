package tbs.framework.mq;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import javax.annotation.Resource;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author abstergo
 */
public abstract class AbstractMessageCenter implements InitializingBean, DisposableBean {

    private AtomicBoolean started = new AtomicBoolean(false);

    @Resource
    private List<IMessageConsumer> messageConsumerList;

    public static void checkInputConsumer(IMessageConsumer messageConsumer) {
        if (messageConsumer == null || StrUtil.isEmpty(messageConsumer.consumerId())) {
            throw new NullPointerException("消费者为空或消息者id为空");
        }
        if (CollUtil.isEmpty(messageConsumer.avaliableTopics())) {
            throw new NoSuchElementException("none topic to listen");
        }
    }

    /**
     * 消息中心事件
     *
     * @return 如果返回的Optional为空（即Optional.empty()），则表明没有可用的事件处理器,以下方法会失效
     *     {@link AbstractMessageCenter#messageArrived(IMessage)} {@link AbstractMessageCenter#messageSent(IMessage)}
     *     {@link AbstractMessageCenter#handleMessageError(IMessage, int, IMessageQueueEvents.MessageHandleType,
     *     Throwable, IMessageConsumer)} {@link AbstractMessageCenter#errorOnRecive(int, Throwable)}
     *     {@link AbstractMessageCenter#errorOnSend(IMessage, int, Throwable)}
     *     {@link AbstractMessageCenter#errorOnConsume(IMessage, int, Throwable, IMessageConsumer)}
     */
    protected abstract Optional<IMessageQueueEvents> getMessageQueueEvents();

    /**
     * 为消费准备的消费管理器
     *
     * @return 如果返回的Optional为空（即Optional.empty()），则表明没有可用的消费处理器,以下方法会失效
     *     {@link AbstractMessageCenter#appendConsumer(IMessageConsumer)}
     *     {@link AbstractMessageCenter#remove(IMessageConsumer)}
     */
    protected abstract Optional<IMessageConsumerManager> getMessageConsumerManager();

    /**
     * 设置信息中心是否启动
     *
     * @param started
     */
    protected void setStarted(boolean started) {
        this.started.set(started);
    }

    /**
     * 消息中心是否启动
     *
     * @return
     */
    public boolean isStart() {
        return started.get();
    }

    /**
     * 新增消息消费者
     *
     * @param messageConsumer
     * @return
     */

    public AbstractMessageCenter appendConsumer(IMessageConsumer messageConsumer) {
        getMessageConsumerManager().orElseThrow(() -> {
            return new UnsupportedOperationException("none consumer manager");
        }).setMessageConsumer(messageConsumer);
        return this;
    }

    /**
     * 移除消息消费者
     *
     * @param consumer
     * @return
     */

    public AbstractMessageCenter remove(IMessageConsumer consumer) {
        getMessageConsumerManager().orElseThrow(() -> {
            return new UnsupportedOperationException("none consumer manager");
        }).removeMessageConsumer(consumer);
        return this;
    }

    /**
     * 消息抵达并进行消费
     *
     * @param msg
     * @return 是否成功被消费
     */
    public boolean messageArrived(IMessage msg) {
        return getMessageQueueEvents().orElseThrow(() -> {
            return new UnsupportedOperationException("none events avaliable");
        }).onMessageReceived(msg);
    }

    /**
     * 消息已发送回调
     *
     * @param msg
     */
    public void messageSent(IMessage msg) {
        getMessageQueueEvents().orElseThrow(() -> {
            return new UnsupportedOperationException("none events avaliable");
        }).onMessageSent(msg);
    }

    /**
     * 消息异常处理回调
     *
     * @param msg      异常消息
     * @param r        重试次数
     * @param type     操作类型
     * @param e        异常
     * @param consumer 异常消费者
     * @return 是否重试
     */
    public boolean handleMessageError(IMessage msg, int r, IMessageQueueEvents.MessageHandleType type, Throwable e,
        IMessageConsumer consumer) {
        return getMessageQueueEvents().orElseThrow(() -> {
            return new UnsupportedOperationException("none events avaliable");
        }).onMessageFailed(msg, r, type, e, consumer);
    }

    /**
     * 消费时消费处理
     *
     * @param m 异常消息
     * @param r 重试次数
     * @param e 异常
     * @param c 异常消费者
     * @return 是否重试
     * @see #handleMessageError(IMessage, int, IMessageQueueEvents.MessageHandleType, Throwable, IMessageConsumer)
     */
    public boolean errorOnConsume(IMessage m, int r, Throwable e, IMessageConsumer c) {
        return handleMessageError(m, r, IMessageQueueEvents.MessageHandleType.Consume, e, c);
    }

    /**
     * 接收时异常处理
     *
     * @param r 重试次数
     * @param e 异常
     * @return 是否重试
     * @see #handleMessageError(IMessage, int, IMessageQueueEvents.MessageHandleType, Throwable, IMessageConsumer)
     */
    public boolean errorOnRecive(int r, Throwable e) {
        return handleMessageError(null, r, IMessageQueueEvents.MessageHandleType.Receive, e, null);
    }

    /**
     * 发送时异常处理
     *
     * @param m 异常的消息
     * @param r 重试次数
     * @param e 异常
     * @return 是否重试
     * @see #handleMessageError(IMessage, int, IMessageQueueEvents.MessageHandleType, Throwable, IMessageConsumer)
     */
    public boolean errorOnSend(IMessage m, int r, Throwable e) {
        return handleMessageError(m, r, IMessageQueueEvents.MessageHandleType.Send, e, null);
    }

    /**
     * 发布消息
     *核心业务实现自{@link #sendMessage(IMessage)}
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

    /**
     * 初始化时自动调用以下方法
     *
     * @throws Exception
     * @see #setStarted(boolean)
     * @see #centerStartToWork()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        setStarted(true);
        for (IMessageConsumer messageConsumer : messageConsumerList) {
            if (messageConsumer == null) {
                continue;
            }
            this.appendConsumer(messageConsumer);
        }
        centerStartToWork();
    }

    /**
     * @throws Exception
     * @see #setStarted(boolean)
     * @see #centerStopToWork()
     */
    @Override
    public void destroy() throws Exception {
        setStarted(false);
        centerStopToWork();
    }
}
