package tbs.framework.redis.impls;

import lombok.Data;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import tbs.framework.base.lock.expections.ObtainLockFailException;
import tbs.framework.base.log.ILogger;
import tbs.framework.base.utils.LogUtil;
import tbs.framework.mq.AbstractMessageCenter;
import tbs.framework.mq.IMessage;
import tbs.framework.mq.IMessageReceiver;
import tbs.framework.redis.properties.RedisProperty;

/**
 * @author abstergo
 */
public class RedisMessageReceiver implements IMessageReceiver {

    public static final String TOPIC_PREFIX = "MESSAGE_CENTER.";

    private RedisMessageListenerContainer listenerContainer;

    public RedisMessageListenerContainer listenerContainer() {
        return listenerContainer;
    }

    RedisTaksBlockLock redisTaksBlockLock;

    RedisProperty redisProperty;

    MessageListenerAdapter adapter;

    public RedisMessageReceiver(RedisMessageListenerContainer listenerContainer, AbstractMessageCenter center,
        RedisProperty property, RedisTaksBlockLock redisTaksBlockLock) {
        this.listenerContainer = listenerContainer;
        this.redisTaksBlockLock = redisTaksBlockLock;
        this.redisProperty = property;
        RedisMessageConsumer redisMessageConsumer = new RedisMessageConsumer(center);
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
    @Deprecated
    public IMessage receive() {
        throw new RuntimeException("no implement because RedisMessageReceiver will handle message auto");
    }

    @Data
    private class RedisMessageConsumer {
        private AbstractMessageCenter center;
        private ILogger logger = null;

        private ILogger getLogger() {
            if (logger == null) {
                logger = LogUtil.getInstance().getLogger(this.getClass().getName());
            }
            return logger;
        }

        public RedisMessageConsumer(AbstractMessageCenter center) {
            this.center = center;
        }

        private static final String LOCK_KEY = "MESSAGE_CENTER_BLOCK_KEY";

        public void consume(IMessage message) {
            try {
                if (redisProperty.isMessageHandleOnce()) {
                    redisTaksBlockLock.lock(LOCK_KEY + message.getMessageId());
                }
                if (!center.messageArrived(message)) {
                    center.publish(message);
                }
            } catch (ObtainLockFailException r) {
                getLogger().warn("get lock to fail:{}", r.getMessage());
            } finally {
                redisTaksBlockLock.unlock(LOCK_KEY + message.getMessageId());
            }
        }
    }

}
