package tbs.framework.base.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import tbs.framework.mq.IMessageQueue;
import tbs.framework.mq.impls.event.BaseMessageQueueEvent;
import tbs.framework.mq.impls.event.EmptySentAndErrorEventImpl;
import tbs.framework.mq.impls.queue.SimpleMessageQueue;

@Data
@ConfigurationProperties(prefix = "tbs.framework.mq")
public class MqProperty {
    /**
     * 队列容器的实现
     */
    private Class<? extends IMessageQueue> queueImpl = SimpleMessageQueue.class;

    private Class<? extends BaseMessageQueueEvent> eventImpl = EmptySentAndErrorEventImpl.class;
}
