package tbs.framework.mq.center;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import tbs.framework.mq.consumer.IMessageConsumer;
import tbs.framework.mq.consumer.manager.IMessageConsumerManager;
import tbs.framework.mq.event.IMessageQueueEvents;
import tbs.framework.mq.message.IMessage;
import tbs.framework.mq.receiver.IMessageReceiver;
import tbs.framework.mq.sender.IMessagePublisher;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author abstergo
 */
public abstract class AbstractMessageCenter implements InitializingBean, DisposableBean {
    
    private AtomicBoolean started = new AtomicBoolean(false);

    private AtomicBoolean listening = new AtomicBoolean(false);

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
     *@return 实现的消息接收器
     */
    protected abstract Optional<IMessageReceiver> getMessageReceiver();

    /**
     *@return 实现的消息发布器
     */
    protected abstract Optional<IMessagePublisher> getMessagePublisher();

    /**
     * 消息中心事件
     * @return 实现的事件器
     */
    protected abstract Optional<IMessageQueueEvents> getMessageQueueEvents();

    /**
     * 为消费准备的消费管理器
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
     * @param message 消息
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
     * 针对一个消息采用默认的
     * {@link IMessageConsumerManager#selectMessageConsumer(IMessage)}
     * 选择消费者并进行批量消费
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
     */
    public void messageArrived(IMessage msg) {
        getMessageQueueEvents().orElseThrow(() -> {
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
     * @param msg 异常消息
     * @param r 重试次数
     * @param type 操作类型
     * @param e 异常
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
     * @see #handleMessageError(IMessage, int, IMessageQueueEvents.MessageHandleType, Throwable, IMessageConsumer)
     * @return 是否重试
     */
    public boolean errorOnConsume(IMessage m, int r, Throwable e, IMessageConsumer c) {
        return handleMessageError(m, r, IMessageQueueEvents.MessageHandleType.Consume, e, c);
    }

    /**
     * 接收时异常处理
     *
     * @param r 重试次数
     * @param e 异常
     * @see #handleMessageError(IMessage, int, IMessageQueueEvents.MessageHandleType, Throwable, IMessageConsumer)
     * @return 是否重试
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
     * @see #handleMessageError(IMessage, int, IMessageQueueEvents.MessageHandleType, Throwable, IMessageConsumer)
     * @return 是否重试
     */
    public boolean errorOnSend(IMessage m, int r, Throwable e) {
        return handleMessageError(m, r, IMessageQueueEvents.MessageHandleType.Send, e, null);
    }

    /**
     * 发布消息
     *由publisher实现 {@link #getMessagePublisher()}
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
        if (!isStart()) {
            throw new RuntimeException("center is not started");
        }
        IMessageReceiver receiver = getMessageReceiver().orElseThrow(() -> {
            return new UnsupportedOperationException("none receiver");
        });
        if (!receiver.avaliableReceive()) {
            String addtional = receiver.avaliablePull() ? "pull mode is on,receiver will auto handle message"
                : "pull mode is not avaliable too.";
            throw new RuntimeException("receiver receive mode is not avaliable ." + addtional);
        }
        if (isListen()) {
            throw new RuntimeException("center is already listen");
        }
        this.listening.set(true);
        for (int i = 0; i < thread; i++) {
            executorService.execute(() -> {
                while (isStart() && isListen() && receiver.avaliableReceive()) {
                    IMessage msg = receiver.receive();
                    if (msg == null) {
                        continue;
                    }
                    messageArrived(msg);
                    consumeMessage(msg);
                }
            });
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

    /**
     * 初始化时自动调用以下方法
     */
    @Override
    public void afterPropertiesSet() throws Exception {
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
