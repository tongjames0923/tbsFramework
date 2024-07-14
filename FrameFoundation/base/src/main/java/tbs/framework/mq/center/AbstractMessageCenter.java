package tbs.framework.mq.center;

import cn.hutool.core.collection.CollUtil;
import org.springframework.beans.factory.DisposableBean;
import tbs.framework.base.utils.LogFactory;
import tbs.framework.log.ILogger;
import tbs.framework.log.annotations.AutoLogger;
import tbs.framework.mq.center.impls.AbstractCommonMessageCenterEventImpl;
import tbs.framework.mq.center.interfaces.IMessageCenterConsumeSupport;
import tbs.framework.mq.center.interfaces.IMessageReceiverManagerSupport;
import tbs.framework.mq.center.interfaces.IMessageSenderSupport;
import tbs.framework.mq.connector.IMessageConnector;
import tbs.framework.mq.consumer.IMessageConsumer;
import tbs.framework.mq.consumer.manager.IMessageConsumerManager;
import tbs.framework.mq.message.IMessage;
import tbs.framework.mq.sender.IMessagePublisher;
import tbs.framework.utils.IStartup;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 抽象消息中心类。
 *
 * @author abstergo
 */
public abstract class AbstractMessageCenter extends AbstractCommonMessageCenterEventImpl
    implements IStartup, DisposableBean, IMessageCenterConsumeSupport, IMessageReceiverManagerSupport,
    IMessageSenderSupport {


    private AtomicBoolean started = new AtomicBoolean(false);



    private AtomicBoolean warnForEmptyConsumer = new AtomicBoolean(false);

    protected void tooManyRetryTimes(int n) {
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
     * 获取消息接收连接器。
     *
     * @return 消息接收连接器。
     */
    public abstract IMessageConnector getConnector();


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
     * 对消息采用默认的消费者管理器选择消费者并进行批量消费。
     *
     * @param message 消息。
     */
    public void consumeMessage(IMessage message) {
        getMessageConsumerManager().ifPresentOrElse(manager -> {
            List<IMessageConsumer> consumers = manager.selectMessageConsumer(message);
            if (checkConsumersAvaliable(consumers)) {
                return;
            }
            for (IMessageConsumer consumer : consumers) {
                executeConsume(message, manager, consumer);

            }
        }, () -> {
            throw new NoSuchElementException("no consumer manager");
        });
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
        centerStopToWork();
        getConnector().destoryReceivers(this, getReceivers());
        getConnector().destoryPublishers(this, getMessagePublisher());
    }

    private void executeConsume(IMessage message, IMessageConsumerManager manager, IMessageConsumer consumer) {
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

    private boolean checkConsumersAvaliable(List<IMessageConsumer> consumers) {
        if (CollUtil.isEmpty(consumers)) {
            if (!warnForEmptyConsumer.get()) {
                getLogger().warn("no consumer to consume message");
                warnForEmptyConsumer.set(true);
            }
            return true;
        }
        return false;
    }
}
