package tbs.framework.rabbitmq.receivers;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import tbs.framework.mq.connector.IMessageConnector;
import tbs.framework.mq.message.IMessage;
import tbs.framework.mq.receiver.IMessageReceiver;
import tbs.framework.rabbitmq.connectors.RabbitMqManulReceiveConnector;

/**
 * 主动接收形式的rabbitMq消息接收器
 *
 * @author Abstergo
 */
public class ManulRabbitReceiver implements IMessageReceiver {
    private RabbitMqManulReceiveConnector connector;

    private RabbitTemplate template;

    private String queueName;

    public ManulRabbitReceiver(RabbitMqManulReceiveConnector connector, RabbitTemplate template, String queueName) {
        this.connector = connector;
        this.template = template;
        this.queueName = queueName;
    }

    @Override
    public IMessage receive() {
        Message message = template.receive(queueName);
        if (message == null) {
            return null;
        }
        IMessage message1 = (IMessage)template.getMessageConverter().fromMessage(message);
        return message1;
    }

    @Override
    public IMessageConnector builder() {
        return connector;
    }

    @Override
    public void pull(IMessage message) {

    }
}
