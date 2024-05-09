package tbs.framework.redis.impls;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import tbs.framework.mq.AbstractMessageCenter;
import tbs.framework.mq.IMessage;
import tbs.framework.mq.IMessageConsumer;
import tbs.framework.redis.properties.RedisProperty;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;

public class RedisMessageCenter extends AbstractMessageCenter {

    RedisMessageListenerContainer listenerContainer;

    @Resource(name = "REDIS_MSG")
    private RedisTemplate<String, Object> redisTemplate;

    public RedisMessageCenter(RedisMessageListenerContainer listenerContainer) {
        this.listenerContainer = listenerContainer;

    }

    @Resource
    RedisProperty redisProperty;

    @Override
    public AbstractMessageCenter setMessageConsumer(IMessageConsumer messageConsumer) {
        checkInputConsumer(messageConsumer);
        List<ChannelTopic> list = new LinkedList<>();
        MessageListenerAdapter adapter = new MessageListenerAdapter(messageConsumer, "consume");
        adapter.setSerializer(new JdkSerializationRedisSerializer());
        adapter.afterPropertiesSet();
        for (String k : messageConsumer.avaliableTopics()) {
            list.add(new ChannelTopic(k));
        }
        listenerContainer.addMessageListener(adapter, list);

        return this;
    }

    @Override
    public boolean removeMessageConsumer(IMessageConsumer messageConsumer) {
        return false;
    }

    @Override
    protected void sendMessage(IMessage message) {
        redisTemplate.convertAndSend(message.getTopic(), message);
    }

    @Override
    protected boolean onMessageReceived(IMessage message) {
        return false;
    }

    @Override
    protected void onMessageSent(IMessage message) {

    }

    @Override
    protected boolean onMessageFailed(IMessage message, int retryed, MessageHandleType type, Throwable throwable,
        IMessageConsumer consumer) {

        return false;
    }

    @Override
    protected List<IMessageConsumer> selectMessageConsumer(IMessage message) {
        return List.of();
    }

    @Override
    public void centerStartToWork() {
        this.listenerContainer.start();
    }

    @Override
    public void centerStopToWork() {
        this.listenerContainer.stop();
    }

    @Override
    public boolean isStart() {
        return false;
    }
}
