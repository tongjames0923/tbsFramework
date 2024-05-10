package tbs.framework.redis.impls;

import lombok.Data;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import tbs.framework.mq.IMessage;
import tbs.framework.mq.IMessageQueue;
import tbs.framework.mq.IMessageReceiver;

/**
 * @author abstergo
 */
public class RedisMessageReceiver implements IMessageReceiver {

    public static final String TOPIC_PREFIX = "MESSAGE_CENTER.";

    private RedisMessageListenerContainer listenerContainer;
    private IMessageQueue messageQueue;

    public RedisMessageListenerContainer listenerContainer() {
        return listenerContainer;
    }

    public IMessageQueue messageQueue() {
        return messageQueue;
    }

    MessageListenerAdapter adapter;

    public RedisMessageReceiver(RedisMessageListenerContainer listenerContainer, IMessageQueue messageQueue) {
        this.listenerContainer = listenerContainer;
        this.messageQueue = messageQueue;
        RedisMessageConsumer redisMessageConsumer = new RedisMessageConsumer(messageQueue);
        adapter = new MessageListenerAdapter(redisMessageConsumer, "consume");
        adapter.setSerializer(new JdkSerializationRedisSerializer());
        listenerContainer.addMessageListener(adapter, new PatternTopic("MESSAGE_CENTER.*"));
    }

    public void begin() {
        adapter.afterPropertiesSet();
        listenerContainer.start();
    }

    public void end() {
        listenerContainer.stop();
    }

    @Override
    public IMessage receive() {
        return messageQueue.getNext();
    }

    @Data
    private static class RedisMessageConsumer {
        private IMessageQueue messageQueue;

        public RedisMessageConsumer(IMessageQueue messageQueue) {
            this.messageQueue = messageQueue;
        }

        public void consume(IMessage message) {
            messageQueue.insert(message);
        }
    }

}
