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
import tbs.framework.mq.sender.impls.MessageQueueSender;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * @author Abstergo
 */
public class MessageQueueCenter extends AbstractMessageCenter {

    private IMessagePublisher publisher = new MessageQueueSender(this);

    List<IMessageReceiver> receivers = null;

    @Override
    public Optional<IMessageConnector> getConnector() {
        return Optional.of(SpringUtil.getBean(MessageQueueConnector.class));
    }

    public MessageQueueCenter() {
    }

    @Override
    protected Optional<IMessagePublisher> getMessagePublisher() {
        return Optional.of(publisher);
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
        List<IMessageReceiver> okReceivers = new LinkedList<>();
        for (IMessageReceiver receiverI : receiver) {
            if (receiverI instanceof QueueReceiver) {
                receivers.add(receiverI);
                okReceivers.add(receiverI);
            }
        }
        factoryReceivers(okReceivers);
    }

    private void factoryReceivers(List<IMessageReceiver> receivers) {
        getConnector().ifPresent((c) -> {
            c.factoryMessageReceivers(receivers);
        });
    }

    private void unfactoryReceivers(IMessageReceiver receiver) {
        getConnector().ifPresent((c) -> {
            c.invalidateReceivers(Arrays.asList(receiver));
        });
    }

    @Override
    public void removeReceivers(IMessageReceiver... receiver) {
        for (IMessageReceiver receiverI : receiver) {
            if (receiverI instanceof QueueReceiver) {
                unfactoryReceivers(receiverI);
            }
        }
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
        factoryReceivers(getReceivers());
        listen(Executors.newFixedThreadPool(3), 3);
    }

    @Override
    protected void centerStopToWork() {

    }
}
