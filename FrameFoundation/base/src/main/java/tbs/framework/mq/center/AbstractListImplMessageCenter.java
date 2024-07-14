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
 * 消息中心抽象实现。
 * <p>
 * 此抽象类继承自{@link AbstractListenableMessageCenter}，实现了大部分功能，子类只需要提供连接器即可。
 * 该类使用了Spring框架，通过{@link Resource}注解注入了{@link IMessagePublisher}、{@link IMessageReceiver}、
 * {@link IMessageConsumerManager}和{@link IMessageQueueEvents}的实例。
 * </p>
 *
 * @author Abstergo
 * @see AbstractListenableMessageCenter
 * @see SpringUtil
 * @see IMessagePublisher
 * @see IMessageReceiver
 * @see IMessageConsumerManager
 * @see IMessageQueueEvents
 * @since 1.0.0
 */
public abstract class AbstractListImplMessageCenter extends AbstractListenableMessageCenter {

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
    public Optional<IMessageQueueEvents> getMessageQueueEvents() {
        return Optional.of(events);
    }

    @Override
    public Optional<IMessageConsumerManager> getMessageConsumerManager() {
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

    @Override
    protected void centerStopToWork() {

    }
}
