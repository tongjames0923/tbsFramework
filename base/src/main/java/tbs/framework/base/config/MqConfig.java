package tbs.framework.base.config;

import org.springframework.context.annotation.Bean;
import tbs.framework.base.properties.MqProperty;
import tbs.framework.mq.connector.impls.MessageQueueConnector;
import tbs.framework.mq.consumer.manager.IMessageConsumerManager;
import tbs.framework.mq.consumer.manager.impls.MappedConsumerManager;
import tbs.framework.mq.event.IMessageQueueEvents;
import tbs.framework.mq.event.impls.EmptySentAndErrorEventImpl;
import tbs.framework.mq.receiver.impls.QueueReceiver;

import javax.annotation.Resource;


/**
 * @author abstergo
 */
public class MqConfig {

    @Bean
    MessageQueueConnector messageQueueConnector() {
        return new MessageQueueConnector();
    }

    @Bean
    QueueReceiver queueReceiver() {
        return new QueueReceiver();
    }

}
