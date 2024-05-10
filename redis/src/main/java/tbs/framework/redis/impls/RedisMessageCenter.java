package tbs.framework.redis.impls;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import tbs.framework.base.log.ILogger;
import tbs.framework.base.utils.LogUtil;
import tbs.framework.mq.AbstractMessageCenter;
import tbs.framework.mq.IMessage;
import tbs.framework.mq.IMessageConsumerManager;
import tbs.framework.mq.IMessageQueueEvents;
import tbs.framework.redis.properties.RedisProperty;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * @author abstergo
 */
public class RedisMessageCenter extends AbstractMessageCenter {

    private RedisMessageReceiver receiver;
    private ILogger logger = null;

    @Resource(name = "REDIS_MSG")
    private RedisTemplate<String, Object> redisTemplate;

    private IMessageQueueEvents messageQueueEvents;
    private IMessageConsumerManager messageConsumerManager;

    public RedisMessageCenter(RedisMessageListenerContainer messageListenerContainer, IMessageConsumerManager cm,
        IMessageQueueEvents qe, RedisProperty property, RedisTaksBlockLock blockLock) {
        messageConsumerManager = cm;
        messageQueueEvents = qe;
        this.receiver = new RedisMessageReceiver(messageListenerContainer, this, property, blockLock);
    }

    @Override
    protected void centerStopToWork() {
        receiver.end();
    }

    @Override
    protected void centerStartToWork() {
        receiver.begin();
    }

    @Override
    protected Optional<IMessageQueueEvents> getMessageQueueEvents() {
        return Optional.ofNullable(messageQueueEvents);
    }

    @Override
    protected Optional<IMessageConsumerManager> getMessageConsumerManager() {
        return Optional.ofNullable(messageConsumerManager);
    }

    @Override
    protected void sendMessage(IMessage message) {
        redisTemplate.convertAndSend(RedisMessageReceiver.TOPIC_PREFIX + message.getTopic(), message);
    }

    private ILogger getLogger() {
        if (logger == null) {
            logger = LogUtil.getInstance().getLogger(this.getClass().getName());
        }
        return logger;
    }
}
