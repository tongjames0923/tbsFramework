package tbs.framework.base.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import tbs.framework.mq.consumer.manager.IMessageConsumerManager;
import tbs.framework.mq.consumer.manager.impls.MappedConsumerManager;
import tbs.framework.mq.event.IMessageQueueEvents;
import tbs.framework.mq.event.impls.EmptySentAndErrorEventImpl;

import java.time.Duration;

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
     * 消费者管理器的实现¬
     */
    private Class<? extends IMessageConsumerManager> consumerManager = MappedConsumerManager.class;

    /**
     * 任务块存活时间，任务锁占有时间
     */
    private Duration taskBlockAliveTime = Duration.ofMinutes(5);

    /**
     * 任务块清理间隔,消息处理完成后锁存在时间
     */
    private Duration taskBlockCleanInterval = Duration.ofMinutes(1);
}
