package tbs.framework.redis.impls;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import tbs.framework.base.lock.expections.ObtainLockFailException;
import tbs.framework.base.log.ILogger;
import tbs.framework.base.utils.LogUtil;
import tbs.framework.mq.*;
import tbs.framework.redis.properties.RedisProperty;

import java.util.LinkedList;
import java.util.List;

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

    AbstractMessageCenter messageCenter;

    private IMessageConsumerManager consumerManager;

    public RedisMessageReceiver(RedisMessageListenerContainer listenerContainer, AbstractMessageCenter center,
        RedisProperty property, RedisTaksBlockLock redisTaksBlockLock, IMessageConsumerManager consumerManager) {
        this.listenerContainer = listenerContainer;
        this.redisTaksBlockLock = redisTaksBlockLock;
        this.redisProperty = property;
        this.messageCenter = center;
        this.consumerManager = consumerManager;


    }

    public void begin() {
        for (IMessageConsumer consumer : consumerManager.getConsumers()) {

            if (consumer == null) {
                continue;
            }
            boolean flag = false;
            List<PatternTopic> topics = new LinkedList<>();
            for (String i : consumer.avaliableTopics()) {
                if (StrUtil.isEmpty(i)) {
                    continue;
                }
                topics.add(new PatternTopic(TOPIC_PREFIX + "*" + i));
                flag = true;
            }
            if (!flag) {
                continue;
            }
            RedisMessageConsumer redisMessageConsumer = new RedisMessageConsumer(messageCenter, consumer);
            MessageListenerAdapter adapter = new MessageListenerAdapter(redisMessageConsumer, "consume");
            adapter.setSerializer(new JdkSerializationRedisSerializer());
            listenerContainer.addMessageListener(adapter, topics);
            adapter.afterPropertiesSet();
        }
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
        private IMessageConsumer consumer;

        private ILogger getLogger() {
            if (logger == null) {
                logger = LogUtil.getInstance().getLogger(this.getClass().getName());
            }
            return logger;
        }

        public RedisMessageConsumer(AbstractMessageCenter center, IMessageConsumer consumer) {
            this.center = center;
            this.consumer = consumer;
        }

        private static final String LOCK_KEY = "MESSAGE_CENTER_BLOCK_KEY";

        public void consume(IMessage message) {
            try {
                if (redisProperty.isMessageHandleOnce()) {
                    redisTaksBlockLock.lock(lockId(message));
                }
                center.messageArrived(message);
                center.consumeMessages(consumer, message);
            } catch (ObtainLockFailException r) {
                getLogger().warn("get lock to fail:{}", r.getMessage());
            } finally {
                redisTaksBlockLock.unlock(lockId(message));
            }
        }

        private @NotNull String lockId(IMessage message) {
            return LOCK_KEY + message.getMessageId() + consumer.consumerId();
        }
    }

}
