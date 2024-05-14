package tbs.framework.mq.center;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.DisposableBean;
import tbs.framework.log.ILogger;
import tbs.framework.utils.IStartup;
import tbs.framework.utils.LogUtil;
import tbs.framework.mq.connector.IMessageConnector;
import tbs.framework.mq.consumer.IMessageConsumer;
import tbs.framework.mq.consumer.manager.IMessageConsumerManager;
import tbs.framework.mq.event.IMessageQueueEvents;
import tbs.framework.mq.message.IMessage;
import tbs.framework.mq.receiver.IMessageReceiver;
import tbs.framework.mq.receiver.impls.AbstractIdentityReceiver;
import tbs.framework.mq.sender.IMessagePublisher;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

/**
 * @author abstergo
 */
public abstract class AbstractMessageCenter implements IStartup, DisposableBean {

    private AtomicBoolean started = new AtomicBoolean(false);

    private AtomicBoolean listening = new AtomicBoolean(false);

    ILogger logger;

    private ILogger getLogger() {
        if (logger == null) {
            synchronized (this) {
                logger = LogUtil.getInstance().getLogger(this.getClass().getName());
            }
        }
        return logger;
    }

    /**
     * 检查消息消费器是否合法
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
     * @return 实现的消息接收连接器
     */
    public abstract Optional<IMessageConnector> getConnector();

    /**
     * @return 实现的消息发布器
     */
    protected abstract Optional<IMessagePublisher> getMessagePublisher();

    public abstract List<IMessageReceiver> getReceivers();

    public abstract void addReceivers(IMessageReceiver... receiver);

    public abstract void removeReceivers(IMessageReceiver... receiver);

    /**
     * 消息中心事件
     *
     * @return 实现的事件器
     */
    protected abstract Optional<IMessageQueueEvents> getMessageQueueEvents();

    /**
     * 为消费准备的消费管理器
     *
     * @return 实现的消费者管理器
     */
    protected abstract Optional<IMessageConsumerManager> getMessageConsumerManager();

    /**
     *
     */
    public List<IMessageConsumer> allConsumersInCenter() {
        return getMessageConsumerManager().map(IMessageConsumerManager::getConsumers).orElseThrow(() -> {
            return new UnsupportedOperationException("consumer manager is not avaliable");
        });
    }

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
     */
    public boolean isStart() {
        return started.get();
    }

    /**
     * 新增消息消费者
     *
     * @param messageConsumer
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
     */
    public AbstractMessageCenter remove(IMessageConsumer consumer) {
        getMessageConsumerManager().orElseThrow(() -> {
            return new UnsupportedOperationException("none consumer manager");
        }).removeMessageConsumer(consumer);
        return this;
    }

    /**
     * 针对一个消费者批量消息消息
     *
     * @param consumer 消费者
     * @param message  消息
     */
    public void consumeMessages(IMessageConsumer consumer, IMessage... message) {
        IMessageConsumerManager manager = getMessageConsumerManager().orElseThrow(() -> {
            return new UnsupportedOperationException("none consumer manager");
        });
        for (IMessage msg : message) {
            if (msg == null) {
                continue;
            }
            manager.consumeOnce(this, consumer, msg);
        }
    }

    /**
     * 针对一个消息采用默认的 {@link IMessageConsumerManager#selectMessageConsumer(IMessage)} 选择消费者并进行批量消费
     *
     * @param message 信息
     */
    public void consumeMessage(IMessage message) {
        IMessageConsumerManager manager = getMessageConsumerManager().orElseThrow(() -> {
            return new UnsupportedOperationException("none consumer manager");
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
     * 消息抵达触发
     *
     * @param msg
     * @param connector
     * @param receiver
     */
    public void messageArrived(IMessage msg, IMessageConnector connector, IMessageReceiver receiver) {
        getMessageQueueEvents().orElseThrow(() -> {
            return new UnsupportedOperationException("none events avaliable");
        }).onMessageReceived(msg, connector, receiver);
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
     * 发布消息 由publisher实现 {@link #getMessagePublisher()}
     *
     * @param message 信息
     */
    public void publish(IMessage message) {
        if (message == null) {
            throw new NullPointerException("message is null");
        }
        IMessagePublisher publisher = getMessagePublisher().orElseThrow(() -> {
            return new UnsupportedOperationException("none publisher");
        });
        int tryTimes = 0;
        while (true) {
            try {
                publisher.publishAll(message);
                break;
            } catch (Exception e) {
                if (getMessageQueueEvents().map((ev) -> {
                    return !ev.onMessageFailed(message, tryTimes, IMessageQueueEvents.MessageHandleType.Send, e, null);
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
     * 是否在监听
     */
    public boolean isListen() {
        return listening.get();
    }

    /**
     * 启动监听（即启动消息接收器）由{@link #getMessageReceiver()}实现
     *
     * @param thread          线程数
     * @param executorService 异步服务
     */
    public void listen(ExecutorService executorService, int thread) {
        PreCheckResult pre = preCheckResult();
        this.listening.set(true);
        toListen(executorService, thread, pre);
    }

    private void toListen(ExecutorService executorService, int thread, PreCheckResult pre) {
        for (int i = 0; i < thread; i++) {
            executorService.execute(() -> {
                while (isStart() && isListen()) {
                    mainListenTask(pre);
                }
            });
        }
    }

    private void mainListenTask(PreCheckResult pre) {
        try {
            BigDecimal index = pre.currentRec.getAndAccumulate(BigDecimal.ONE, new BinaryOperator<BigDecimal>() {
                @Override
                public BigDecimal apply(BigDecimal bigDecimal, BigDecimal bigDecimal2) {
                    return bigDecimal.add(bigDecimal2);
                }
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
        IMessageConnector connector = getConnector().orElseThrow(() -> {
            return new UnsupportedOperationException("none connector");
        });

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
        Map<Integer, IMessageReceiver> receivers = getReceivers().stream().collect(Collectors.toMap((p) -> {
            return cnt.getAndIncrement();
        }, (p) -> {
            return p;
        }));
        if (CollUtil.isEmpty(receivers)) {
            throw new NoSuchElementException("receivers is empty");
        }
        ReceiverPreHandle receiverHandle = new ReceiverPreHandle(currentRec, cnt, receivers);
        return receiverHandle;
    }

    private static class ReceiverPreHandle {
        public final AtomicReference<BigDecimal> currentRec;
        public final AtomicInteger cnt;
        public final Map<Integer, IMessageReceiver> receivers;

        public ReceiverPreHandle(AtomicReference<BigDecimal> currentRec, AtomicInteger cnt,
            Map<Integer, IMessageReceiver> receivers) {
            this.currentRec = currentRec;
            this.cnt = cnt;
            this.receivers = receivers;
        }
    }

    private static class PreCheckResult {
        public final IMessageConnector connector;
        public final AtomicReference<BigDecimal> currentRec;
        public final AtomicInteger cnt;
        public final Map<Integer, IMessageReceiver> receivers;

        public PreCheckResult(IMessageConnector connector, AtomicReference<BigDecimal> currentRec, AtomicInteger cnt,
            Map<Integer, IMessageReceiver> receivers) {
            this.connector = connector;
            this.currentRec = currentRec;
            this.cnt = cnt;
            this.receivers = receivers;
        }
    }

    /**
     * 停止监听消息
     */
    public void stopListen() {
        listening.set(false);
    }

    /**
     * 消息中心启动
     */
    protected abstract void centerStartToWork();

    /**
     * 消息中心停止
     */
    protected abstract void centerStopToWork();

    @Override
    public void startUp() throws RuntimeException {
        setStarted(true);
        centerStartToWork();
    }

    /**
     *
     */
    @Override
    public void destroy() throws Exception {
        setStarted(false);
        centerStopToWork();
    }
}
