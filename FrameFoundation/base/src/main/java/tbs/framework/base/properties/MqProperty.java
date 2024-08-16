package tbs.framework.base.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import tbs.framework.mq.center.AbstractListenableMessageCenter;
import tbs.framework.mq.center.impls.MessageQueueCenter;
import tbs.framework.mq.connector.IMessageConnector;
import tbs.framework.mq.connector.impls.MessageQueueConnector;
import tbs.framework.mq.consumer.manager.IMessageConsumerManager;
import tbs.framework.mq.consumer.manager.impls.PatternConsumerManager;

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
     * 消费者管理器的实现
     */
    private Class<? extends IMessageConsumerManager> consumerManager = PatternConsumerManager.class;

    /**
     * 消息中心实现
     */
    private Class<? extends AbstractListenableMessageCenter> listenableMessageCenter = MessageQueueCenter.class;

    /**
     * 消息连接器实现
     */
    private Class<? extends IMessageConnector> messageCenterConnector = MessageQueueConnector.class;

    /**
     * 任务块存活时间，任务锁占有时间
     */
    private Duration taskBlockAliveTime = Duration.ofMinutes(5);

    /**
     * 任务块清理间隔,消息处理完成后锁存在时间
     */
    private Duration taskBlockCleanInterval = Duration.ofMinutes(1);

}

