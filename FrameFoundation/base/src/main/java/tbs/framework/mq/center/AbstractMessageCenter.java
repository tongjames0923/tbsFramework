package tbs.framework.mq.center;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.DisposableBean;
import tbs.framework.base.utils.LogFactory;
import tbs.framework.log.ILogger;
import tbs.framework.log.annotations.AutoLogger;
import tbs.framework.mq.connector.IMessageConnector;
import tbs.framework.mq.consumer.IMessageConsumer;
import tbs.framework.mq.consumer.manager.IMessageConsumerManager;
import tbs.framework.mq.event.IMessageQueueEvents;
import tbs.framework.mq.message.IMessage;
import tbs.framework.mq.receiver.IMessageReceiver;
import tbs.framework.mq.receiver.impls.AbstractIdentityReceiver;
import tbs.framework.mq.sender.IMessagePublisher;
import tbs.framework.utils.IStartup;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 抽象消息中心类。
 *
 * @author abstergo
 */
public abstract class AbstractMessageCenter implements IStartup, DisposableBean {


    private AtomicBoolean started = new AtomicBoolean(false);

    private AtomicBoolean listening = new AtomicBoolean(false);

    private AtomicBoolean warnForEmptyConsumer = new AtomicBoolean(false);

    private void tooManyRetryTimes(int n) {
        if (n >= 128) {
            throw new RuntimeException("too many retry times");
        }
    }

    @AutoLogger
    private ILogger logger;

    protected synchronized ILogger getLogger() {
        if (logger == null) {
            logger = LogFactory.Companion.getInstance().getLogger(AbstractMessageCenter.class.getName());
        }
        return logger;
    }

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
     * 获取消息接收连接器。
     *
     * @return 消息接收连接器。
     */
    public abstract IMessageConnector getConnector();

    /**
     * 获取消息发布器。
     *
     * @return 消息发布器。
     */
    public abstract IMessagePublisher getMessagePublisher();

    /**
     * 设置消息发布器。
     *
     * @param messagePublisher 消息发布器。
     */
    public abstract void setMessagePublisher(IMessagePublisher messagePublisher);

    /**
     * 获取消息接收器列表。
     *
     * @return 消息接收器列表。
     */
    public abstract List<IMessageReceiver> getReceivers();

    /**
     * 添加消息接收器。
     *
     * @param receiver 消息接收器。
     */
    public abstract void addReceivers(IMessageReceiver... receiver);

    /**
     * 移除消息接收器。
     *
     * @param receiver 消息接收器。
     */
    public abstract void removeReceivers(IMessageReceiver... receiver);

    /**
     * 获取消息队列事件处理器。
     *
     * @return 消息队列事件处理器。
     */
    public abstract Optional<IMessageQueueEvents> getMessageQueueEvents();

    /**
     * 获取消息消费者管理器。
     *
     * @return 消息消费者管理器。
     */
    public abstract Optional<IMessageConsumerManager> getMessageConsumerManager();

    /**
     * 获取消息中心中的所有消费者。
     *
     * @return 消息消费者列表。
     */
    public List<IMessageConsumer> allConsumersInCenter() {
        return getMessageConsumerManager().map(IMessageConsumerManager::getConsumers).orElse(CollUtil.newArrayList());
    }

    /**
     * 设置消息中心是否启动。
     *
     * @param started 是否启动。
     */
    protected void setStarted(boolean started) {
        this.started.set(started);
    }

    /**
     * 检查消息中心是否启动。
     *
     * @return 是否启动。
     */
    public boolean isStart() {
        return started.get();
    }

    /**
     * 添加消息消费者。
     *
     * @param messageConsumer 消息消费者。
     * @return 当前消息中心对象。
     */
    public AbstractMessageCenter appendConsumer(IMessageConsumer messageConsumer) {
        getMessageConsumerManager().ifPresent(c -> {
            c.setMessageConsumer(messageConsumer);
        });
        return this;
    }

    /**
     * 移除消息消费者。
     *
     * @param consumer 消息消费者。
     * @return 当前消息中心对象。
     */
    public AbstractMessageCenter remove(IMessageConsumer consumer) {
        getMessageConsumerManager().ifPresent(c -> {
            c.removeMessageConsumer(consumer);
        });
        return this;
    }

    /**
     * 对消息采用默认的消费者管理器选择消费者并进行批量消费。
     *
     * @param message 消息。
     */
    public void consumeMessage(IMessage message) {
        getMessageConsumerManager().ifPresentOrElse(manager -> {
            List<IMessageConsumer> consumers = manager.selectMessageConsumer(message);
            if (CollUtil.isEmpty(consumers)) {
                if (!warnForEmptyConsumer.get()) {
                    getLogger().warn("no consumer to consume message");
                    warnForEmptyConsumer.set(true);
                }
                return;
            }
            for (IMessageConsumer consumer : consumers) {
                while (true) {
                    int r = 0;
                    try {
                        manager.consumeOnce(this, consumer, message);
                        break;
                    } catch (Exception e) {
                        if (!errorOnConsume(message, r++, e, consumer)) {
                            break;
                        }
                    }
                    tooManyRetryTimes(r);
                }

            }
        }, () -> {
            throw new NoSuchElementException("no consumer manager");
        });
    }

    /**
     * 消息抵达触发。
     *
     * @param msg       消息。
     * @param connector 消息连接器。
     * @param receiver  消息接收器。
     */
    public void messageArrived(IMessage msg, IMessageConnector connector, IMessageReceiver receiver) {
        getMessageQueueEvents().ifPresent(c -> c.onMessageReceived(msg, connector, receiver));
    }

    /**
     * 消息已发送回调。
     *
     * @param msg 消息。
     */
    public void messageSent(IMessage msg) {
        getMessageQueueEvents().ifPresent((ev) -> {
            ev.onMessageSent(msg);
        });
    }

    /**
     * 消息异常处理回调。
     *
     * @param msg      异常消息。
     * @param r        重试次数。
     * @param type     操作类型。
     * @param e        异常。
     * @param consumer 异常消费者。
     * @return 是否重试。
     */
    public boolean handleMessageError(IMessage msg, int r, IMessageQueueEvents.MessageHandleType type, Throwable e,
        IMessageConsumer consumer) {
        return getMessageQueueEvents().map((m) -> {
            return m.onMessageFailed(msg, r, type, e, consumer);
        }).orElse(false);
    }

    /**
     * 消费时异常处理。
     *
     * @param m 异常消息。
     * @param r 重试次数。
     * @param e 异常。
     * @param c 异常消费者。
     * @return 是否重试。
     */
    public boolean errorOnConsume(IMessage m, int r, Throwable e, IMessageConsumer c) {
        return handleMessageError(m, r, IMessageQueueEvents.MessageHandleType.Consume, e, c);
    }

    /**
     * 接收时异常处理。
     *
     * @param r 重试次数。
     * @param e 异常。
     * @return 是否重试。
     */
    public boolean errorOnReceive(Throwable e, int r) {
        return handleMessageError(null, r, IMessageQueueEvents.MessageHandleType.Receive, e, null);
    }

    /**
     * 发送时异常处理。
     *
     * @param m 异常的消息。
     * @param r 重试次数。
     * @param e 异常。
     * @return 是否重试。
     */
    public boolean errorOnSend(IMessage m, int r, Throwable e) {
        return handleMessageError(m, r, IMessageQueueEvents.MessageHandleType.Send, e, null);
    }

    /**
     * 发布消息。
     *
     * @param message 消息。
     */
    public void publish(IMessage message) {
        if (message == null) {
            throw new NullPointerException("message is null");
        }
        IMessagePublisher publisher = getMessagePublisher();
        final AtomicInteger tryTimes = new AtomicInteger(0);
        while (true) {
            try {
                publisher.publish(message, this);
                break;
            } catch (Exception ex) {
                if (!errorOnSend(message, tryTimes.incrementAndGet(), ex)) {
                    break;
                }
            }
            tooManyRetryTimes(tryTimes.get());
        }
        getMessageQueueEvents().ifPresent((ev) -> {
            ev.onMessageSent(message);
        });
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
                if (receiver instanceof AbstractIdentityReceiver) {
                    AbstractIdentityReceiver identityReceiver = (AbstractIdentityReceiver)receiver;
                    if (!identityReceiver.avaliable()) {
                        continue;
                    }
                }
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

    private void listenPreCheck() {
        if (!isStart()) {
            throw new IllegalStateException("message center is not started");
        }
        if (isListen()) {
            throw new IllegalStateException("message center is listening");
        }
    }
    /**
     * 停止监听消息。
     */
    public void stopListen() {
        listening.set(false);
    }

    /**
     * 消息中心启动。
     */
    protected abstract void centerStartToWork();

    /**
     * 消息中心停止。
     */
    protected abstract void centerStopToWork();

    /**
     * 设置启动标志为true,使用连接器设置发布器和接收器,执行额外的启动操作。涉及的相关对象包括消息连接器、消息发布器和消息接收器。 配置发布器
     *
     * @throws RuntimeException
     * @see IMessageConnector#createPublishers(AbstractMessageCenter) 配置发布器
     * @see IMessageConnector#createReceivers(AbstractMessageCenter) 配置接收器
     * @see AbstractMessageCenter#getConnector() 获取连接器
     * @see AbstractMessageCenter#centerStartToWork() 额外启动操作
     * @see AbstractMessageCenter#setStarted(boolean) 设置启动标志
     */
    @Override
    public void startUp() throws RuntimeException {
        setStarted(true);
        centerStartToWork();
        getConnector().createPublishers(this);
        getConnector().createReceivers(this);
    }

    @Override
    public void destroy() throws Exception {
        setStarted(false);
        stopListen();
        centerStopToWork();
        getConnector().destoryReceivers(this, getReceivers());
        getConnector().destoryPublishers(this, getMessagePublisher());
    }
}
