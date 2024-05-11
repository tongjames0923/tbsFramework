package tbs.framework.mq.connector.impls;

import cn.hutool.extra.spring.SpringUtil;
import org.springframework.context.annotation.Lazy;
import tbs.framework.mq.center.AbstractMessageCenter;
import tbs.framework.mq.center.impls.MessageQueueCenter;
import tbs.framework.mq.connector.IMessageConnector;
import tbs.framework.mq.message.IMessage;
import tbs.framework.mq.receiver.IMessageReceiver;
import tbs.framework.mq.receiver.impls.QueueReceiver;

import javax.annotation.Resource;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * @author Abstergo
 */
public class MessageQueueConnector implements IMessageConnector {

    @Resource
    @Lazy
    MessageQueueCenter messageQueueCenter;

    private PriorityQueue<IMessage> messageQueue = new PriorityQueue<>(MessageQueueConnector::compare);

    public MessageQueueConnector(MessageQueueCenter messageQueueCenter) {
        this.messageQueueCenter = messageQueueCenter;
    }

    private static int compare(IMessage m1, IMessage m2) {
        return m1.getPriority() > m2.getPriority() ? -1 : 1;
    }

    @Override
    public AbstractMessageCenter getMessageCenter() {
        return messageQueueCenter;
    }

    @Override
    public List<IMessageReceiver> getReceivers() {
        return SpringUtil.getBeansOfType(QueueReceiver.class).values().stream().collect(Collectors.toList());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        getReceivers().forEach((p) -> {
            if (p instanceof QueueReceiver) {
                QueueReceiver queueReceiver = (QueueReceiver)p;
                queueReceiver.setQueue(messageQueue);
            }
        });
        getMessageCenter().listen(Executors.newFixedThreadPool(3), 3);
    }

    @Override
    public void destroy() throws Exception {

    }
}
