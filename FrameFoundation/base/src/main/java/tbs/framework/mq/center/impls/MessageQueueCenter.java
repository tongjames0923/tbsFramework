package tbs.framework.mq.center.impls;

import cn.hutool.extra.spring.SpringUtil;
import org.springframework.context.annotation.Lazy;
import tbs.framework.mq.center.AbstractMessageCenter;
import tbs.framework.mq.connector.IMessageConnector;
import tbs.framework.mq.connector.impls.MessageQueueConnector;
import tbs.framework.mq.consumer.IMessageConsumer;
import tbs.framework.mq.consumer.manager.IMessageConsumerManager;
import tbs.framework.mq.event.IMessageQueueEvents;
import tbs.framework.mq.receiver.IMessageReceiver;
import tbs.framework.mq.sender.IMessagePublisher;
import tbs.framework.utils.ThreadUtil;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author Abstergo
 */
public class MessageQueueCenter extends AbstractMessageCenter {

    private IMessagePublisher publisher;

    private List<IMessageReceiver> receivers = new LinkedList<>();

    @Resource
    @Lazy
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
    public Optional<IMessageQueueEvents> getMessageQueueEvents() {
        return Optional.ofNullable(SpringUtil.getBean(IMessageQueueEvents.class));
    }

    @Override
    public Optional<IMessageConsumerManager> getMessageConsumerManager() {
        return Optional.ofNullable(SpringUtil.getBean(IMessageConsumerManager.class));
    }

    @Override
    protected void centerStartToWork() {
        Collection<IMessageConsumer> consumers = SpringUtil.getBeansOfType(IMessageConsumer.class).values();
        for (IMessageConsumer consumer : consumers) {
            appendConsumer(consumer);
        }
    }

    @Override
    public void startUp() throws RuntimeException {
        super.startUp();
        ThreadUtil.getInstance().runCollectionInBackground(() -> {
            this.listen();
        });
    }

    @Override
    protected void centerStopToWork() {
        stopListen();
    }
}
