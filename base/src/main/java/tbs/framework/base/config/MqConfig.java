package tbs.framework.base.config;

import cn.hutool.extra.spring.SpringUtil;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import tbs.framework.base.properties.MqProperty;
import tbs.framework.mq.center.AbstractMessageCenter;
import tbs.framework.mq.consumer.manager.IMessageConsumerManager;
import tbs.framework.mq.consumer.manager.impls.MappedConsumerManager;
import tbs.framework.mq.event.IMessageQueueEvents;
import tbs.framework.mq.event.impls.EmptySentAndErrorEventImpl;
import tbs.framework.mq.receiver.impls.QueueReceiver;

import javax.annotation.Resource;

public class MqConfig {

    @Resource
    MqProperty mqProperty;

    @Bean
    @ConditionalOnProperty(name = "tbs.framework.mq.auto-start-center", matchIfMissing = true, havingValue = "true")
    public ApplicationRunner autoStartCenter() {
        return new ApplicationRunner() {
            @Override
            public void run(ApplicationArguments args) throws Exception {
                for (AbstractMessageCenter center : SpringUtil.getBeansOfType(AbstractMessageCenter.class).values()) {
                    if (center.isStart()) {
                        continue;
                    }
                    center.beginCenter();
                }
            }
        };
    }

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
