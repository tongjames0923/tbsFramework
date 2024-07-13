package tbs.framework.mq.center;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import org.jetbrains.annotations.NotNull;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 抽象消息中心类。
 *
 * @author abstergo
 */
public abstract class AbstractMessageCenter implements IStartup, DisposableBean {

    // 是否启动的原子布尔值
    private AtomicBoolean started = new AtomicBoolean(false);
    // 是否监听的原子布尔值
    private AtomicBoolean listening = new AtomicBoolean(false);

    // 日志记录器
    @AutoLogger
    private ILogger logger;

    // 获取日志记录器
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
    protected abstract Optional<IMessageQueueEvents> getMessageQueueEvents();

    /**
     * 获取消息消费者管理器。
     *
     * @return 消息消费者管理器。
     */
    protected abstract Optional<IMessageConsumerManager> getMessageConsumerManager();

    /**
     * 获取消息中心中的所有消费者。
     *
     * @return 消息消费者列表。
     */
    public List<IMessageConsumer> allConsumersInCenter() {
        return getMessageConsumerManager().map(IMessageConsumerManager::getConsumers).orElseThrow(() -> {
            throw new UnsupportedOperationException("consumer manager is not available");
        });
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
        getMessageConsumerManager().orElseThrow(() -> {
            throw new UnsupportedOperationException("none consumer manager");
        }).setMessageConsumer(messageConsumer);
        return this;
    }

    /**
     * 移除消息消费者。
     *
     * @param consumer 消息消费者。
     * @return 当前消息中心对象。
     */
    public AbstractMessageCenter remove(IMessageConsumer consumer) {
        getMessageConsumerManager().orElseThrow(() -> {
            throw new UnsupportedOperationException("none consumer manager");
        }).removeMessageConsumer(consumer);
        return this;
    }

    /**
     * 对指定消费者批量消费消息。
     *
     * @param consumer 消费者。
     * @param message  消息。
     */
    public void consumeMessages(IMessageConsumer consumer, IMessage... message) {
        IMessageConsumerManager manager = getMessageConsumerManager().orElseThrow(() -> {
            throw new UnsupportedOperationException("none consumer manager");
        });
        for (IMessage msg : message) {
            if (msg == null) {
                continue;
            }
            manager.consumeOnce(this, consumer, msg);
        }
    }

    /**
     * 对消息采用默认的消费者管理器选择消费者并进行批量消费。
     *
     * @param message 消息。
     */
    public void consumeMessage(IMessage message) {
        IMessageConsumerManager manager = getMessageConsumerManager().orElseThrow(() -> {
            throw new UnsupportedOperationException("none consumer manager");
        });
        List<IMessageConsumer> consumers = manager.selectMessageConsumer(message);
        if (CollUtil.isEmpty(consumers)) {
            return;
        }
        for (IMessageConsumer consumer : consumers) {
            consumeMessages(consumer, message);
        }
    }

    /**
     * 消息抵达触发。
     *
     * @param msg       消息。
     * @param connector 消息连接器。
     * @param receiver  消息接收器。
     */
    public void messageArrived(IMessage msg, IMessageConnector connector, IMessageReceiver receiver) {
        getMessageQueueEvents().orElseThrow(() -> {
            throw new UnsupportedOperationException("none events available");
        }).onMessageReceived(msg, connector, receiver);
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
        return getMessageQueueEvents().orElseThrow(() -> {
            throw new UnsupportedOperationException("none events available");
        }).onMessageFailed(msg, r, type, e, consumer);
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
    public boolean errorOnReceive(int r, Throwable e) {
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
                publisher.publishAll(message);
                break;
            } catch (Exception ex) {
                tryTimes.incrementAndGet();
                if (getMessageQueueEvents().map((ev) -> {
                    return !ev.onMessageFailed(message, tryTimes.get(), IMessageQueueEvents.MessageHandleType.Send, ex,
                        null);
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
        PreCheckResult pre = preCheckResult();
        this.listening.set(true);
        while (isStart() && isListen()) {
            mainListenTask(pre);
        }
        this.listening.set(false);
    }

    private void mainListenTask(PreCheckResult pre) {
        try {
            BigDecimal index = pre.currentRec.getAndAccumulate(BigDecimal.ONE, (bigDecimal, bigDecimal2) -> {
                return bigDecimal.add(bigDecimal2);
            }).divideAndRemainder(BigDecimal.valueOf(pre.cnt.get()))[1];
            IMessageReceiver receiver = pre.receivers.getOrDefault(index.intValue(), null);
            if (receiver == null) {
                throw new NoSuchElementException("receiver index[" + index.intValue() + "] is not in map");
            }
            if (receiver instanceof AbstractIdentityReceiver) {
                AbstractIdentityReceiver identityReceiver = (AbstractIdentityReceiver)receiver;
                if (!identityReceiver.avaliable()) {
                    return;
                }
            }
            IMessage msg = receiver.receive();
            if (msg == null) {
                return;
            }
            messageArrived(msg, pre.connector, receiver);
            consumeMessage(msg);
        } catch (Exception e) {
            getLogger().error(e, "error occurred on listen");
        }
    }

    private @NotNull PreCheckResult preCheckResult() {
        if (!isStart()) {
            throw new RuntimeException("center is not started");
        }
        IMessageConnector connector = getConnector();

        if (isListen()) {
            throw new RuntimeException("center is already listen");
        }
        ReceiverPreHandle receiverHandle = receiverHandleTask(connector);
        PreCheckResult pre =
            new PreCheckResult(connector, receiverHandle.currentRec, receiverHandle.cnt, receiverHandle.receivers);
        return pre;
    }

    private @NotNull ReceiverPreHandle receiverHandleTask(IMessageConnector connector) {
        AtomicReference<BigDecimal> currentRec = new AtomicReference<>(BigDecimal.ZERO);
        AtomicInteger cnt = new AtomicInteger(0);
        Map<Integer, IMessageReceiver> receivers =
            getReceivers().stream().collect(Collectors.toMap(p -> cnt.getAndIncrement(), p -> p));
        if (CollUtil.isEmpty(receivers)) {
            throw new NoSuchElementException("receivers is empty");
        }
        ReceiverPreHandle receiverHandle = new ReceiverPreHandle(currentRec, cnt, receivers);
        return receiverHandle;
    }

    private static class ReceiverPreHandle {
        /**
         * 当前接收器索引的原子引用。
         */
        public final AtomicReference<BigDecimal> currentRec;

        /**
         * 接收器数量的原子整数。
         */
        public final AtomicInteger cnt;

        /**
         * 接收器映射，键为接收器索引，值为接收器对象。
         */
        public final Map<Integer, IMessageReceiver> receivers;

        /**
         * 构造函数，初始化当前接收器索引、接收器数量和接收器映射。
         *
         * @param currentRec 当前接收器索引的原子引用。
         * @param cnt        接收器数量的原子整数。
         * @param receivers  接收器映射。
         */
        public ReceiverPreHandle(AtomicReference<BigDecimal> currentRec, AtomicInteger cnt,
            Map<Integer, IMessageReceiver> receivers) {
            this.currentRec = currentRec;
            this.cnt = cnt;
            this.receivers = receivers;
        }
    }

    private static class PreCheckResult {
        /**
         * 消息连接器。
         */
        public final IMessageConnector connector;

        /**
         * 当前接收器索引的原子引用。
         */
        public final AtomicReference<BigDecimal> currentRec;

        /**
         * 接收器数量的原子整数。
         */
        public final AtomicInteger cnt;

        /**
         * 接收器映射，键为接收器索引，值为接收器对象。
         */
        public final Map<Integer, IMessageReceiver> receivers;

        /**
         * 构造函数，初始化消息连接器、当前接收器索引、接收器数量和接收器映射。
         *
         * @param connector  消息连接器。
         * @param currentRec 当前接收器索引的原子引用。
         * @param cnt        接收器数量的原子整数。
         * @param receivers  接收器映射。
         */
        public PreCheckResult(IMessageConnector connector, AtomicReference<BigDecimal> currentRec, AtomicInteger cnt,
            Map<Integer, IMessageReceiver> receivers) {
            this.connector = connector;
            this.currentRec = currentRec;
            this.cnt = cnt;
            this.receivers = receivers;
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
        centerStopToWork();
        getConnector().destoryReceivers(this, getReceivers());
        getConnector().destoryPublishers(this, getMessagePublisher());
        setStarted(false);
    }
}
