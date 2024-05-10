package tbs.framework.redis.impls;

import lombok.Data;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import tbs.framework.base.lock.expections.ObtainLockFailException;
import tbs.framework.base.log.ILogger;
import tbs.framework.base.utils.LogUtil;
import tbs.framework.mq.IMessage;
import tbs.framework.mq.IMessageQueue;
import tbs.framework.mq.IMessageReceiver;
import tbs.framework.redis.properties.RedisProperty;

import javax.annotation.Resource;

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

    @Resource
    RedisBlockLock redisBlockLock;

    @Resource
    RedisProperty redisProperty;

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
    private class RedisMessageConsumer {
        private IMessageQueue messageQueue;

        ILogger logger = null;

        private ILogger getLogger() {
            if (logger == null) {
                logger = LogUtil.getInstance().getLogger(this.getClass().getName());
            }
            return logger;
        }
        public RedisMessageConsumer(IMessageQueue messageQueue) {
            this.messageQueue = messageQueue;
        }

        private static final String LOCK_KEY = "MESSAGE_CENTER_BLOCK_KEY";

        public void consume(IMessage message) {
            try {
                if (redisProperty.isMessageHandleOnce()) {
                    redisBlockLock.lock(LOCK_KEY + message.getMessageId());
                }
                messageQueue.insert(message);
                redisBlockLock.unlock(LOCK_KEY + message.getMessageId());
            } catch (ObtainLockFailException r) {
                getLogger().warn("get lock to fail:{}", r.getMessage());
            }
        }
    }

}
