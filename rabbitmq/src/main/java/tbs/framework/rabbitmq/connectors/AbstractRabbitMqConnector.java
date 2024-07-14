package tbs.framework.rabbitmq.connectors;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import tbs.framework.mq.center.AbstractMessageCenter;
import tbs.framework.mq.connector.IMessageConnector;
import tbs.framework.mq.consumer.IMessageConsumer;
import tbs.framework.mq.receiver.IMessageReceiver;
import tbs.framework.mq.sender.IMessagePublisher;
import tbs.framework.rabbitmq.properties.RabbitMqProperty;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Abstergo
 */
public abstract class AbstractRabbitMqConnector implements IMessageConnector {
    @Resource
    private RabbitAdmin reabbitAdmin;

    @Resource
    private RabbitMqProperty rabbitMqProperty;

    private List<Queue> queues = new LinkedList<>();

    private List<Binding> bindings = new LinkedList<>();

    @Resource
    private Exchange exchange;

    public Exchange exchange() {
        return exchange;
    }

    public RabbitAdmin reabbitAdmin() {
        return reabbitAdmin;
    }

    public RabbitMqProperty rabbitMqProperty() {
        return rabbitMqProperty;
    }

    public AbstractRabbitMqConnector(RabbitMqProperty rabbitMqProperty) {
        this.rabbitMqProperty = rabbitMqProperty;
        for (String queueName : rabbitMqProperty.getQueues()) {
            queues.add(new Queue(queueName));
        }

    }

    protected abstract IMessagePublisher createPublisher(AbstractMessageCenter center);

    protected abstract IMessageReceiver createReceiver(Queue queue, AbstractMessageCenter center);

    @Override
    public void createPublishers(AbstractMessageCenter center) {
        center.setMessagePublisher(createPublisher(center));
    }

    @Override
    public void createReceivers(AbstractMessageCenter center) {
        resetBinding();
        if (rabbitMqProperty.isRebuildExchangeAndQueue()) {
            reabbitAdmin.declareExchange(exchange);
        }
        for (IMessageConsumer consumer : center.allConsumersInCenter()) {
            if (consumer == null) {
                continue;
            }
            setQueues(center, consumer);
        }
    }

    private void setQueues(AbstractMessageCenter center, IMessageConsumer consumer) {
        for (Queue queue : queues) {
            if (rabbitMqProperty.isRebuildExchangeAndQueue()) {
                reabbitAdmin.declareQueue(queue);
            }

            for (String routingKey : consumer.avaliableTopics()) {
                Binding binding = BindingBuilder.bind(queue).to(exchange).with(routingKey).noargs();
                reabbitAdmin.declareBinding(binding);
                bindings.add(binding);
            }
            center.getReceivers().add(createReceiver(queue, center));
        }
    }

    private void resetBinding() {
        for (Binding binding : bindings) {
            reabbitAdmin.removeBinding(binding);
        }
        bindings.clear();
    }

    @Override
    public void destoryPublishers(AbstractMessageCenter center, IMessagePublisher publishers) {

    }

    @Override
    public void destoryReceivers(AbstractMessageCenter center, List<IMessageReceiver> receivers) {
    }
}
