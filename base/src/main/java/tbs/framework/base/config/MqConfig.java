package tbs.framework.base.config;

import org.springframework.context.annotation.Bean;
import tbs.framework.mq.connector.impls.MessageQueueConnector;
import tbs.framework.mq.receiver.impls.AbstractIdentityReceiver;
import tbs.framework.mq.receiver.impls.QueueReceiver;


/**
 * @author abstergo
 */
public class MqConfig {

    @Bean
    MessageQueueConnector messageQueueConnector() {
        return new MessageQueueConnector();
    }

    @Bean
    AbstractIdentityReceiver queueReceiver() {
        return new QueueReceiver().setAvaliable(true).setId("main-queueReceiver");
    }

}
