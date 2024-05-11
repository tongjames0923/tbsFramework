package tbs.framework.redis.impls.mq;

import cn.hutool.extra.spring.SpringUtil;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import tbs.framework.base.log.ILogger;
import tbs.framework.base.utils.LogUtil;
import tbs.framework.mq.center.AbstractMessageCenter;
import tbs.framework.mq.consumer.IMessageConsumer;
import tbs.framework.mq.consumer.manager.IMessageConsumerManager;
import tbs.framework.mq.event.IMessageQueueEvents;
import tbs.framework.mq.receiver.IMessageReceiver;
import tbs.framework.mq.sender.IMessagePublisher;
import tbs.framework.redis.impls.lock.RedisTaksBlockLock;
import tbs.framework.redis.impls.mq.receiver.RedisMessageReceiver;
import tbs.framework.redis.impls.mq.sender.RedisSender;
import tbs.framework.redis.properties.RedisProperty;

import java.util.Collection;
import java.util.Optional;

/**
 * @author abstergo
 */
public class RedisMessageCenter extends AbstractMessageCenter {

    private RedisMessageReceiver receiver;
    private ILogger logger = null;

    private IMessagePublisher publisher;


    private IMessageQueueEvents messageQueueEvents;
    private IMessageConsumerManager messageConsumerManager;

    public RedisMessageCenter(RedisMessageListenerContainer container, RedisProperty redisProperty,
        RedisTaksBlockLock blockLock, RedisSender sender, IMessageQueueEvents queueEvents,
        IMessageConsumerManager consumerManager) {
        this.receiver = new RedisMessageReceiver(this, container, redisProperty, blockLock);
        this.publisher = sender;
        this.messageConsumerManager = consumerManager;
        this.messageQueueEvents = queueEvents;
    }

    @Override
    protected void centerStopToWork() {
        try {
            receiver.destroy();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void centerStartToWork() {
        Collection<IMessageConsumer> consumers = SpringUtil.getBeansOfType(IMessageConsumer.class).values();
        for (IMessageConsumer consumer : consumers) {
            appendConsumer(consumer);
        }
        try {
            receiver.afterPropertiesSet();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Optional<IMessageReceiver> getMessageReceiver() {
        return Optional.ofNullable(receiver);
    }

    @Override
    protected Optional<IMessagePublisher> getMessagePublisher() {
        return Optional.ofNullable(publisher);
    }

    @Override
    protected Optional<IMessageQueueEvents> getMessageQueueEvents() {
        return Optional.ofNullable(messageQueueEvents);
    }

    @Override
    protected Optional<IMessageConsumerManager> getMessageConsumerManager() {
        return Optional.ofNullable(messageConsumerManager);
    }


    private ILogger getLogger() {
        if (logger == null) {
            logger = LogUtil.getInstance().getLogger(this.getClass().getName());
        }
        return logger;
    }
}
