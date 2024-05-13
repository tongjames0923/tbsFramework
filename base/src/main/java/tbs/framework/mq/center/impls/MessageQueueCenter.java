package tbs.framework.mq.center.impls;

import cn.hutool.extra.spring.SpringUtil;
import tbs.framework.mq.center.AbstractMessageCenter;
import tbs.framework.mq.connector.IMessageConnector;
import tbs.framework.mq.connector.impls.MessageQueueConnector;
import tbs.framework.mq.consumer.IMessageConsumer;
import tbs.framework.mq.consumer.manager.IMessageConsumerManager;
import tbs.framework.mq.event.IMessageQueueEvents;
import tbs.framework.mq.sender.IMessagePublisher;
import tbs.framework.mq.sender.impls.MessageQueueSender;

import java.util.Collection;
import java.util.Optional;

/**
 * @author Abstergo
 */
public class MessageQueueCenter extends AbstractMessageCenter {

    private IMessageConnector connector = new MessageQueueConnector(this);
    private IMessagePublisher publisher = new MessageQueueSender(this);

    @Override
    public Optional<IMessageConnector> getConnector() {
        return Optional.of(connector);
    }

    @Override
    protected Optional<IMessagePublisher> getMessagePublisher() {
        return Optional.of(publisher);
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
        getConnector().map((p) -> {
            try {
                p.startUp();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return p;
        });
    }

    @Override
    protected void centerStopToWork() {
        getConnector().map((p) -> {
            try {
                p.destroy();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return p;
        });
    }
}
