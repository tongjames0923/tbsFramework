package tbs.framework.redis.impls;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import tbs.framework.base.log.ILogger;
import tbs.framework.base.utils.LogUtil;
import tbs.framework.mq.AbstractMessageCenter;
import tbs.framework.mq.IMessage;
import tbs.framework.mq.IMessageConsumer;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;

/**
 * @author abstergo
 */
public abstract class AbstractRedisMessageCenter extends AbstractMessageCenter {

    private RedisMessageListenerContainer listenerContainer;

    private ILogger logger = null;

    @Resource(name = "REDIS_MSG")
    private RedisTemplate<String, Object> redisTemplate;

    public AbstractRedisMessageCenter(RedisMessageListenerContainer listenerContainer) {
        this.listenerContainer = listenerContainer;
    }

    private ILogger getLogger() {
        if (logger == null) {
            logger = LogUtil.getInstance().getLogger(this.getClass().getName());
        }
        return logger;
    }

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
    @Deprecated
    public boolean removeMessageConsumer(IMessageConsumer messageConsumer) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    @Deprecated
    public List<IMessageConsumer> selectMessageConsumer(IMessage message) {
        return List.of();
    }

    @Override
    protected void sendMessage(IMessage message) {
        redisTemplate.convertAndSend(message.getTopic(), message);
    }

    @Override
    public void centerStartToWork() {
        setStarted(true);
        this.listenerContainer.start();
    }

    @Override
    public void centerStopToWork() {
        this.listenerContainer.stop();
        setStarted(false);
    }

    @Override
    @Deprecated
    public boolean onMessageReceived(IMessage message) {
        return false;
    }

}
