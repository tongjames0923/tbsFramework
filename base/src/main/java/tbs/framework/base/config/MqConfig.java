package tbs.framework.base.config;

import org.springframework.context.annotation.Bean;
import tbs.framework.base.properties.MqProperty;
import tbs.framework.mq.IMessageConsumerManager;
import tbs.framework.mq.IMessageQueue;
import tbs.framework.mq.impls.consumer.manager.MappedConsumerManager;
import tbs.framework.mq.impls.event.BaseMessageQueueEvent;
import tbs.framework.mq.impls.event.EmptySentAndErrorEventImpl;
import tbs.framework.mq.impls.queue.SimpleMessageQueue;

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
