package tbs.framework.rabbitmq.connectors;

import org.springframework.amqp.core.Queue;
import tbs.framework.mq.center.AbstractMessageCenter;
import tbs.framework.mq.receiver.IMessageReceiver;
import tbs.framework.mq.sender.IMessagePublisher;
import tbs.framework.rabbitmq.properties.RabbitMqProperty;
import tbs.framework.rabbitmq.receivers.ManulRabbitReceiver;
import tbs.framework.rabbitmq.sender.RabbitMqSender;

public class RabbitMqActiveReceiveConnector extends AbstractRabbitMqConnector {

    public RabbitMqActiveReceiveConnector(RabbitMqProperty rabbitMqProperty) {
        super(rabbitMqProperty);
    }

    @Override
    protected IMessagePublisher createPublisher(AbstractMessageCenter center) {
        return new RabbitMqSender(this.reabbitAdmin().getRabbitTemplate(), this);
    }

    @Override
    protected IMessageReceiver createReceiver(Queue queue, AbstractMessageCenter center) {
        return new ManulRabbitReceiver(this, this.reabbitAdmin().getRabbitTemplate(), queue.getName());
    }
}
