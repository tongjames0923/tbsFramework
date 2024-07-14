package tbs.framework.rabbitmq.connectors;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.AbstractAdaptableMessageListener;
import tbs.framework.base.properties.MqProperty;
import tbs.framework.mq.center.AbstractMessageCenter;
import tbs.framework.mq.connector.IMessageConnector;
import tbs.framework.mq.message.IMessage;
import tbs.framework.mq.receiver.IMessageReceiver;
import tbs.framework.mq.receiver.impls.DirectConsumeReceiver;
import tbs.framework.mq.sender.IMessagePublisher;
import tbs.framework.rabbitmq.properties.RabbitMqProperty;
import tbs.framework.rabbitmq.sender.RabbitMqSender;

import javax.annotation.Resource;

public class RabbitMqPasstiveReceiveConnector extends AbstractRabbitMqConnector {

    @Resource
    ConnectionFactory connectionFactory;

    @Resource
    MqProperty mqProperty;

    public RabbitMqPasstiveReceiveConnector(RabbitMqProperty rabbitMqProperty) {
        super(rabbitMqProperty);
    }

    @Override
    protected IMessagePublisher createPublisher(AbstractMessageCenter center) {
        return new RabbitMqSender(this.reabbitAdmin().getRabbitTemplate(), this);
    }

    @Override
    protected IMessageReceiver createReceiver(Queue queue, AbstractMessageCenter center) {

        //生成 监听容器
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        //设置启动监听超时时间
        container.setReceiveTimeout(rabbitMqProperty().getReceiveTimeout());
        //        container.setExposeListenerChannel(true);
        //设置确认模式 设置成自动偷偷懒~
        container.setAcknowledgeMode(rabbitMqProperty().getAcknowledgeMode());
        container.setAutoDeclare(rabbitMqProperty().isRebuildExchangeAndQueue());
        DirectConsumeReceiver directConsumeReceiver = new DirectConsumeReceiver(this, null, center, mqProperty, null);
        container.setQueues(queue);
        IMessageConnector connector = this;
        container.setMessageListener(new AbstractAdaptableMessageListener() {
            @Override
            public void onMessage(Message message, Channel channel) throws Exception {
                if (message == null) {
                    return;
                }
                IMessage mbody =
                    (IMessage)reabbitAdmin().getRabbitTemplate().getMessageConverter().fromMessage(message);
                directConsumeReceiver.consumeDirectly(mbody);
                if (rabbitMqProperty().getAcknowledgeMode() == AcknowledgeMode.MANUAL) {
                    channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                }
            }
        });
        container.start();
        return directConsumeReceiver;
    }
}
