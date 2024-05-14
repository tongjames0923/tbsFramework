package tbs.framework.redis.impls.mq;

import cn.hutool.extra.spring.SpringUtil;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import tbs.framework.log.ILogger;
import tbs.framework.mq.center.AbstractMessageCenter;
import tbs.framework.mq.connector.IMessageConnector;
import tbs.framework.mq.consumer.IMessageConsumer;
import tbs.framework.mq.consumer.manager.IMessageConsumerManager;
import tbs.framework.mq.event.IMessageQueueEvents;
import tbs.framework.mq.receiver.IMessageReceiver;
import tbs.framework.mq.sender.IMessagePublisher;
import tbs.framework.redis.impls.lock.RedisTaksBlockLock;
import tbs.framework.redis.impls.mq.receiver.RedisChannelReceiver;
import tbs.framework.redis.impls.mq.receiver.RedisMessageConnector;
import tbs.framework.redis.impls.mq.sender.RedisSender;
import tbs.framework.redis.properties.RedisProperty;
import tbs.framework.utils.BeanUtil;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author abstergo
 */
public class RedisMessageCenter extends AbstractMessageCenter {

    private ILogger logger = null;

    private IMessagePublisher publisher;

    private IMessageQueueEvents messageQueueEvents;
    private IMessageConsumerManager messageConsumerManager;

    private List<IMessageReceiver> messageReceivers;

    private RedisTaksBlockLock taksBlockLock;

    public RedisMessageCenter(RedisMessageListenerContainer container, RedisProperty redisProperty,
        RedisTaksBlockLock blockLock, RedisSender sender, IMessageQueueEvents queueEvents,
        IMessageConsumerManager consumerManager) {
        this.publisher = sender;
        this.messageConsumerManager = consumerManager;
        this.messageQueueEvents = queueEvents;
        taksBlockLock = blockLock;
    }

    @Override
    protected void centerStopToWork() {

    }

    @Resource
    AutowireCapableBeanFactory autowireCapableBeanFactory;

    @Override
    protected void centerStartToWork() {
        Collection<IMessageConsumer> consumers = SpringUtil.getBeansOfType(IMessageConsumer.class).values();
        IMessageConnector connector = getConnector().orElseThrow(() -> {
            return new UnsupportedOperationException("none connector");
        });
        for (IMessageConsumer consumer : consumers) {
            appendConsumer(consumer);
            RedisChannelReceiver receiver = new RedisChannelReceiver();
            BeanUtil.registerBean(receiver, receiver.receiverId());
        }
        for (IMessageReceiver receiver : getReceivers()) {
            addReceivers(receiver);
        }
    }

    @Override
    public Optional<IMessageConnector> getConnector() {
        return Optional.ofNullable(SpringUtil.getBean(RedisMessageConnector.class));
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

    @Override
    public List<IMessageReceiver> getReceivers() {
        if (messageReceivers == null) {
            messageReceivers =
                SpringUtil.getBeansOfType(RedisChannelReceiver.class).values().stream().collect(Collectors.toList());
        }
        return messageReceivers;
    }

    @Override
    public void addReceivers(IMessageReceiver... receiver) {
        List<IMessageReceiver> messageReceivers1 = Arrays.asList(receiver);
        getConnector().orElseThrow(() -> {
            return new UnsupportedOperationException("none connector");
        }).factoryMessageReceivers(messageReceivers1);
    }

    @Override
    public void removeReceivers(IMessageReceiver... receiver) {
        List<IMessageReceiver> messageReceivers1 = Arrays.asList(receiver);
        this.messageReceivers.removeAll(messageReceivers1);
        getConnector().orElseThrow(() -> {
            return new UnsupportedOperationException("none connector");
        }).invalidateReceivers(messageReceivers1);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
