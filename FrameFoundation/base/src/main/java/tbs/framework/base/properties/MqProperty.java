package tbs.framework.base.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import tbs.framework.mq.consumer.manager.IMessageConsumerManager;
import tbs.framework.mq.consumer.manager.impls.MappedConsumerManager;
import tbs.framework.mq.event.IMessageQueueEvents;
import tbs.framework.mq.event.impls.EmptySentAndErrorEventImpl;

/**
 * @author Abstergo
 */
@Data
@ConfigurationProperties(prefix = "tbs.framework.mq")
public class MqProperty {
    /**
     * 消息中心自启动
     */
    private boolean autoStartCenter = true;

    /**
     * 事件处理实现
     */
    private Class<? extends IMessageQueueEvents> eventImpl = EmptySentAndErrorEventImpl.class;

    /**
     * 消费者管理器的实现¬
     */
    private Class<? extends IMessageConsumerManager> consumerManager = MappedConsumerManager.class;
}
