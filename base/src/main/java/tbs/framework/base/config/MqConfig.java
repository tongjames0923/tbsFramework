package tbs.framework.base.config;

import org.springframework.context.annotation.Bean;
import tbs.framework.base.properties.MqProperty;
import tbs.framework.mq.consumer.manager.IMessageConsumerManager;
import tbs.framework.mq.queue.IMessageQueue;
import tbs.framework.mq.consumer.manager.impls.MappedConsumerManager;
import tbs.framework.mq.event.BaseMessageQueueEvent;
import tbs.framework.mq.event.EmptySentAndErrorEventImpl;
import tbs.framework.mq.queue.impls.SimpleMessageQueue;

import javax.annotation.Resource;

public class MqConfig {

    @Resource
    MqProperty mqProperty;

    @Bean
    IMessageQueue messageQueue() throws Exception {
        if (mqProperty.getQueueImpl() == null) {
            return new SimpleMessageQueue();
        }
        return mqProperty.getQueueImpl().getConstructor().newInstance();
    }

    @Bean
    BaseMessageQueueEvent baseMessageQueueEvent(IMessageConsumerManager manager) throws Exception {
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
