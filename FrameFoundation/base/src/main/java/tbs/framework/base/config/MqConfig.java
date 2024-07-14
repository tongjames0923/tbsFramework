package tbs.framework.base.config;

import org.springframework.context.annotation.Bean;
import tbs.framework.mq.center.AbstractMessageCenter;
import tbs.framework.mq.center.impls.MessageQueueCenter;
import tbs.framework.mq.connector.impls.MessageQueueConnector;
import tbs.framework.mq.receiver.impls.MessageSourceReceiver;

import java.util.List;

/**
 * @author abstergo
 */
public class MqConfig {

    @Bean
    AbstractMessageCenter center() {
        return new MessageQueueCenter();
    }


    @Bean
    MessageQueueConnector messageQueueConnector(List<MessageSourceReceiver> receivers) {

        return new MessageQueueConnector();
    }

}
