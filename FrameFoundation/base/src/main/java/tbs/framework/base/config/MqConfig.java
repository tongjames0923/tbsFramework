package tbs.framework.base.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import tbs.framework.base.properties.MqProperty;
import tbs.framework.mq.center.AbstractMessageCenter;
import tbs.framework.mq.center.impls.MessageQueueCenter;
import tbs.framework.mq.connector.IMessageConnector;
import tbs.framework.mq.connector.impls.MessageQueueConnector;
import tbs.framework.mq.receiver.impls.MessageSourceReceiver;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * @author abstergo
 */
public class MqConfig {

    @Resource
    MqProperty mqProperty;

    @Bean
    @ConditionalOnMissingBean(AbstractMessageCenter.class)
    AbstractMessageCenter mainCenter()
        throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return mqProperty.getListenableMessageCenter().getConstructor().newInstance();
    }

    @Bean
    @ConditionalOnMissingBean(IMessageConnector.class)
    IMessageConnector mainCenterConnector()
        throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return mqProperty.getMessageCenterConnector().getConstructor().newInstance();
    }

}
