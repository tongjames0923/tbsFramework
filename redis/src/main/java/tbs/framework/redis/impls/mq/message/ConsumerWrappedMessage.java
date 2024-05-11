package tbs.framework.redis.impls.mq.message;

import tbs.framework.mq.consumer.IMessageConsumer;
import tbs.framework.mq.message.IMessage;

import java.util.Map;

public class ConsumerWrappedMessage implements IMessage {

    private IMessage message;

    private IMessageConsumer consumer;

    private ConsumerWrappedMessage() {

    }

    public static ConsumerWrappedMessage of(IMessage message, IMessageConsumer consumer) {
        ConsumerWrappedMessage consumerWrappedMessage = new ConsumerWrappedMessage();
        consumerWrappedMessage.consumer = consumer;
        consumerWrappedMessage.message = message;
        return consumerWrappedMessage;
    }

    public IMessageConsumer consumer() {
        return consumer;
    }

    @Override
    public String getTopic() {
        return message.getTopic();
    }

    @Override
    public String getTag() {
        return message.getTag();
    }

    @Override
    public String getMessageId() {
        return message.getMessageId();
    }

    @Override
    public long getPriority() {
        return message.getPriority();
    }

    @Override
    public Map<String, Object> parmamterMap() {
        return message.parmamterMap();
    }
}
