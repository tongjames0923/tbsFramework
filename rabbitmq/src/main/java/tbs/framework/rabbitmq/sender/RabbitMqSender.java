package tbs.framework.rabbitmq.sender;

import org.jetbrains.annotations.NotNull;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import tbs.framework.mq.center.AbstractMessageCenter;
import tbs.framework.mq.message.IMessage;
import tbs.framework.mq.sender.IMessagePublisher;
import tbs.framework.rabbitmq.connectors.AbstractRabbitMqConnector;

/**
 * @author Abstergo
 */
public class RabbitMqSender implements IMessagePublisher {

    private RabbitTemplate messagingTemplate;

    private AbstractRabbitMqConnector owner;

    public RabbitMqSender(RabbitTemplate messagingTemplate, AbstractRabbitMqConnector owner) {
        this.messagingTemplate = messagingTemplate;
        this.owner = owner;
    }

    @Override
    public void publish(IMessage m, @NotNull AbstractMessageCenter center) {
        messagingTemplate.convertAndSend(owner.exchange().getName(), m.getTopic(), m);
    }
}
