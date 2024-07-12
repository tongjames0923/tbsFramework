package tbs.framework.base.config;

import org.springframework.context.annotation.Bean;
import tbs.framework.base.properties.MqProperty;
import tbs.framework.mq.connector.impls.MessageQueueConnector;
import tbs.framework.mq.consumer.manager.IMessageConsumerManager;
import tbs.framework.mq.event.IMessageQueueEvents;
import tbs.framework.mq.event.impls.EmptySentAndErrorEventImpl;
import tbs.framework.mq.receiver.impls.QueueReceiver;
import tbs.framework.mq.sender.impls.MessageQueueSender;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author abstergo
 */
public class MqConfig {

    @Resource
    MqProperty mqProperty;

    @Bean
    IMessageQueueEvents baseMessageQueueEvent(IMessageConsumerManager manager) throws Exception {
        if (mqProperty.getEventImpl() == null) {
            return new EmptySentAndErrorEventImpl();
        }
        return mqProperty.getEventImpl().getConstructor().newInstance();
    }

    @Bean
    MessageQueueSender sender() {
        return new MessageQueueSender();
    }

    @Bean
    QueueReceiver queueReceiver() {
        return (QueueReceiver)new QueueReceiver().setAvaliable(true).setId("main-queueReceiver");
    }

    @Bean
    MessageQueueConnector messageQueueConnector(List<QueueReceiver> receivers) {

        return new MessageQueueConnector();
    }

}
