package tbs.framework.rabbitmq.config;

import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import tbs.framework.mq.center.AbstractMessageCenter;
import tbs.framework.rabbitmq.RabbitMqCenter;
import tbs.framework.rabbitmq.connectors.RabbitMqManulReceiveConnector;
import tbs.framework.rabbitmq.constants.RabbitMqConstant;
import tbs.framework.rabbitmq.properties.RabbitMqProperty;

import javax.annotation.Resource;

public class RabbitMqConfig {

    @Resource
    RabbitMqProperty rabbitMqProperty;

    @Bean(RabbitMqConstant.BEAN_EXCHANGE)
    Exchange exchange() throws Exception {
        return rabbitMqProperty.getExchangeType().getConstructor(String.class)
            .newInstance(rabbitMqProperty.getExchangeName());
    }

    /**
     * 当服务器启动时，为每个用户生成一个队列用于接收系统消息 队列名称规则：[sys.msg.userId]  sys.msg.1 sys.msg.2
     */
    @Bean(RabbitMqConstant.BEAN_ADMIN)
    public RabbitAdmin rabbitAdmin(RabbitTemplate template) {
        return new RabbitAdmin(template);
    }

    //    @Bean(RabbitMqConstant.BEAN_LISTENER_CONTAINER)
    //    public SimpleMessageListenerContainer listenerContainer(ConnectionFactory connectionFactory) {
    //        //创建监听器工厂
    //        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
    //        factory.setConnectionFactory(connectionFactory);
    //        factory.setReceiveTimeout(rabbitMqProperty.getReceiveTimeout());
    //        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);
    //        //使用工厂生 成监听器容器
    //        return factory.createListenerContainer();
    //    }

    @Bean(RabbitMqConstant.BEAN_CONNECTOR)
    RabbitMqManulReceiveConnector rabbitMqConnector() {
        return new RabbitMqManulReceiveConnector(rabbitMqProperty);
    }

    @Bean
    AbstractMessageCenter center() {
        return new RabbitMqCenter();
    }

}
