package tbs.framework.mq.center.impls;

import cn.hutool.extra.spring.SpringUtil;
import tbs.framework.mq.center.AbstractMessageCenter;
import tbs.framework.mq.connector.IMessageConnector;
import tbs.framework.mq.connector.impls.MessageQueueConnector;
import tbs.framework.mq.consumer.IMessageConsumer;
import tbs.framework.mq.consumer.manager.IMessageConsumerManager;
import tbs.framework.mq.event.IMessageQueueEvents;
import tbs.framework.mq.receiver.IMessageReceiver;
import tbs.framework.mq.receiver.impls.QueueReceiver;
import tbs.framework.mq.sender.IMessagePublisher;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * @author Abstergo
 */
public class MessageQueueCenter extends AbstractMessageCenter {

    private IMessagePublisher publisher;

    List<IMessageReceiver> receivers = null;

    @Resource
    MessageQueueConnector messageQueueConnector;

    @Override
    public IMessageConnector getConnector() {
        return messageQueueConnector;
    }

    @Override
    public IMessagePublisher getMessagePublisher() {
        return publisher;
    }

    @Override
    public void setMessagePublisher(IMessagePublisher messagePublisher) {
        this.publisher = messagePublisher;
    }

    @Override
    public List<IMessageReceiver> getReceivers() {
        if (receivers == null) {
            receivers = SpringUtil.getBeansOfType(QueueReceiver.class).values().stream().collect(Collectors.toList());
        }
        return receivers;
    }

    @Override
    public void addReceivers(IMessageReceiver... receiver) {
        getReceivers().addAll(Arrays.asList(receiver));
    }

    @Override
    public void removeReceivers(IMessageReceiver... receiver) {
        getReceivers().removeAll(Arrays.asList(receiver));
    }

    @Override
    protected Optional<IMessageQueueEvents> getMessageQueueEvents() {
        return Optional.ofNullable(SpringUtil.getBean(IMessageQueueEvents.class));
    }

    @Override
    protected Optional<IMessageConsumerManager> getMessageConsumerManager() {
        return Optional.ofNullable(SpringUtil.getBean(IMessageConsumerManager.class));
    }

    @Override
    protected void centerStartToWork() {
        Collection<IMessageConsumer> consumers = SpringUtil.getBeansOfType(IMessageConsumer.class).values();
        for (IMessageConsumer consumer : consumers) {
            appendConsumer(consumer);
        }
        listen(Executors.newFixedThreadPool(1), 1);
    }

    @Override
    protected void centerStopToWork() {

    }
}
