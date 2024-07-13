package tbs.framework.rabbitmq.sender;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import tbs.framework.mq.message.IMessage;
import tbs.framework.mq.sender.IMessagePublisher;
import tbs.framework.rabbitmq.connectors.RabbitMqManulReceiveConnector;

/**
 * @author Abstergo
 */
public class RabbitMqSender implements IMessagePublisher {

    private RabbitTemplate messagingTemplate;

    private RabbitMqManulReceiveConnector owner;

    public RabbitMqSender(RabbitTemplate messagingTemplate, RabbitMqManulReceiveConnector owner) {
        this.messagingTemplate = messagingTemplate;
        this.owner = owner;
    }

    @Override
    public void publishAll(IMessage... message) {
        for (IMessage m : message) {
            messagingTemplate.convertAndSend(owner.exchange().getName(), m.getTopic(), m);
        }
    }
}
