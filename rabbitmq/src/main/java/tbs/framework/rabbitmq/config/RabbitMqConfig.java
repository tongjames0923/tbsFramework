package tbs.framework.rabbitmq.config;

import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import tbs.framework.mq.center.AbstractMessageCenter;
import tbs.framework.rabbitmq.RabbitMqCenter;
import tbs.framework.rabbitmq.connectors.AbstractRabbitMqConnector;
import tbs.framework.rabbitmq.connectors.RabbitMqActiveReceiveConnector;
import tbs.framework.rabbitmq.connectors.RabbitMqPasstiveReceiveConnector;
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

    @Bean(RabbitMqConstant.BEAN_CONNECTOR)
    AbstractRabbitMqConnector rabbitMqConnector() {
        if (rabbitMqProperty.isPassiveReception()) {
            return new RabbitMqPasstiveReceiveConnector(rabbitMqProperty);
        } else {
            return new RabbitMqActiveReceiveConnector(rabbitMqProperty);
        }
    }

    @Bean
    AbstractMessageCenter rabbitMqCenter() {
        return new RabbitMqCenter();
    }

}
