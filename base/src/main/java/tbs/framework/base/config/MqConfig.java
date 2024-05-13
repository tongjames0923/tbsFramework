package tbs.framework.base.config;

import org.springframework.context.annotation.Bean;
import tbs.framework.base.properties.MqProperty;
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

    @Resource
    MqProperty mqProperty;

    @Bean
    QueueReceiver queueReceiver() {
        return new QueueReceiver();
    }

    @Bean
    IMessageQueueEvents baseMessageQueueEvent(IMessageConsumerManager manager) throws Exception {
        if (mqProperty.getEventImpl() == null) {
            return new EmptySentAndErrorEventImpl();
        }
        return mqProperty.getEventImpl().getConstructor().newInstance();
    }

    @Bean
    IMessageConsumerManager consumerManager() throws Exception {
        if (mqProperty.getConsumerManager() == null) {
            return new MappedConsumerManager();
        }
        return mqProperty.getConsumerManager().getConstructor().newInstance();
    }
}
