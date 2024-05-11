package tbs.framework.base.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import tbs.framework.mq.consumer.manager.IMessageConsumerManager;
import tbs.framework.mq.consumer.manager.impls.MappedConsumerManager;
import tbs.framework.mq.event.EmptySentAndErrorEventImpl;
import tbs.framework.mq.event.IMessageQueueEvents;
import tbs.framework.mq.queue.IMessageQueue;
import tbs.framework.mq.queue.impls.SimpleMessageQueue;

@Data
@ConfigurationProperties(prefix = "tbs.framework.mq")
public class MqProperty {
    private boolean autoStartCenter = true;

    /**
     * 队列容器的实现
     */
    private Class<? extends IMessageQueue> queueImpl = SimpleMessageQueue.class;

    /**
     * 事件处理实现
     */
    private Class<? extends IMessageQueueEvents> eventImpl = EmptySentAndErrorEventImpl.class;

    /**
     * 消费者管理器的实现¬
     */
    private Class<? extends IMessageConsumerManager> consumerManager = MappedConsumerManager.class;
}
