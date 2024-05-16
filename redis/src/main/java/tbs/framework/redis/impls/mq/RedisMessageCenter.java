package tbs.framework.redis.impls.mq;

import cn.hutool.extra.spring.SpringUtil;
import tbs.framework.log.ILogger;
import tbs.framework.log.annotations.AutoLogger;
import tbs.framework.mq.center.AbstractMessageCenter;
import tbs.framework.mq.connector.IMessageConnector;
import tbs.framework.mq.consumer.IMessageConsumer;
import tbs.framework.mq.consumer.manager.IMessageConsumerManager;
import tbs.framework.mq.event.IMessageQueueEvents;
import tbs.framework.mq.receiver.IMessageReceiver;
import tbs.framework.mq.sender.IMessagePublisher;
import tbs.framework.redis.impls.mq.receiver.RedisMessageConnector;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author abstergo
 */
public class RedisMessageCenter extends AbstractMessageCenter {

    @AutoLogger
    private ILogger logger = null;


    private IMessageQueueEvents messageQueueEvents;
    private IMessageConsumerManager messageConsumerManager;

    private List<IMessageReceiver> messageReceivers = new LinkedList<>();

    @Resource
    RedisMessageConnector connector;

    private IMessagePublisher publisher;

    public RedisMessageCenter(IMessageQueueEvents queueEvents,
        IMessageConsumerManager consumerManager) {
        this.messageConsumerManager = consumerManager;
        this.messageQueueEvents = queueEvents;
    }

    @Override
    protected void centerStopToWork() {

    }

    @Override
    protected void centerStartToWork() {
        Collection<IMessageConsumer> consumers = SpringUtil.getBeansOfType(IMessageConsumer.class).values();
        IMessageConnector connector = getConnector();
        for (IMessageConsumer consumer : consumers) {
            appendConsumer(consumer);
        }

    }

    @Override
    public IMessageConnector getConnector() {
        return connector;
    }

    @Override
    public IMessagePublisher getMessagePublisher() {
        return this.publisher;
    }

    @Override
    public void setMessagePublisher(IMessagePublisher messagePublisher) {
        this.publisher = messagePublisher;
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
        return messageReceivers;
    }

    @Override
    public void addReceivers(IMessageReceiver... receiver) {
        messageReceivers.addAll(Arrays.asList(receiver));
    }

    @Override
    public void removeReceivers(IMessageReceiver... receiver) {
        messageReceivers.removeAll(Arrays.asList(receiver));
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
