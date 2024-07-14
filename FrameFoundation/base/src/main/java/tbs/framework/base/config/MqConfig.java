package tbs.framework.base.config;

import org.springframework.context.annotation.Bean;
import tbs.framework.base.properties.MqProperty;
import tbs.framework.mq.center.AbstractMessageCenter;
import tbs.framework.mq.center.impls.MessageQueueCenter;
import tbs.framework.mq.connector.impls.MessageQueueConnector;
import tbs.framework.mq.receiver.impls.LocalFullFeatureReceiver;
import tbs.framework.mq.sender.impls.LocalProvideSender;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author abstergo
 */
public class MqConfig {

    @Resource
    MqProperty mqProperty;

    @Bean
    AbstractMessageCenter center() {
        return new MessageQueueCenter();
    }

    @Bean
    LocalProvideSender sender() {
        return new LocalProvideSender();
    }

    @Bean
    LocalFullFeatureReceiver queueReceiver() {
        return (LocalFullFeatureReceiver)new LocalFullFeatureReceiver().setAvaliable(true).setId("main-queueReceiver");
    }

    @Bean
    MessageQueueConnector messageQueueConnector(List<LocalFullFeatureReceiver> receivers) {

        return new MessageQueueConnector();
    }

}
