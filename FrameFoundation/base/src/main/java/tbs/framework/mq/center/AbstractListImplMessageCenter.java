package tbs.framework.mq.center;

import cn.hutool.extra.spring.SpringUtil;
import tbs.framework.mq.consumer.IMessageConsumer;
import tbs.framework.mq.consumer.manager.IMessageConsumerManager;
import tbs.framework.mq.event.IMessageQueueEvents;
import tbs.framework.mq.receiver.IMessageReceiver;
import tbs.framework.mq.sender.IMessagePublisher;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author Abstergo
 */
public abstract class AbstractListImplMessageCenter extends AbstractMessageCenter {

    private IMessagePublisher messagePublisher;

    private List<IMessageReceiver> receivers = new LinkedList<>();

    @Resource
    IMessageConsumerManager manager;

    @Resource
    IMessageQueueEvents events;

    @Override
    public IMessagePublisher getMessagePublisher() {
        return messagePublisher;
    }

    @Override
    public void setMessagePublisher(IMessagePublisher messagePublisher) {
        this.messagePublisher = messagePublisher;
    }

    @Override
    public List<IMessageReceiver> getReceivers() {
        return receivers;
    }

    @Override
    public void addReceivers(IMessageReceiver... receiver) {
        receivers.addAll(Arrays.asList(receiver));
    }

    @Override
    public void removeReceivers(IMessageReceiver... receiver) {
        receivers.removeAll(Arrays.asList(receiver));
    }

    @Override
    protected Optional<IMessageQueueEvents> getMessageQueueEvents() {
        return Optional.of(events);
    }

    @Override
    protected Optional<IMessageConsumerManager> getMessageConsumerManager() {
        return Optional.ofNullable(manager);
    }

    @Override
    protected void centerStartToWork() {
        Collection<IMessageConsumer> consumers = SpringUtil.getBeansOfType(IMessageConsumer.class).values();
        //        IMessageConnector connector = getConnector();
        for (IMessageConsumer consumer : consumers) {
            appendConsumer(consumer);
        }

    }

}
