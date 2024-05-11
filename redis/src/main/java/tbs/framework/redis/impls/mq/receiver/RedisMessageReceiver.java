package tbs.framework.redis.impls.mq.receiver;

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
import tbs.framework.mq.center.AbstractMessageCenter;
import tbs.framework.mq.consumer.IMessageConsumer;
import tbs.framework.mq.message.IMessage;
import tbs.framework.mq.receiver.IMessageReceiver;
import tbs.framework.redis.impls.lock.RedisTaksBlockLock;
import tbs.framework.redis.impls.mq.message.ConsumerWrappedMessage;
import tbs.framework.redis.properties.RedisProperty;

import java.util.LinkedList;
import java.util.List;

/**
 * @author abstergo
 */
public class RedisMessageReceiver implements IMessageReceiver {

    /**
     *
     */
    public static final String TOPIC_PREFIX = "MESSAGE_CENTER.";

    /**
     *
     */
    private RedisMessageListenerContainer listenerContainer;

    /**
     *
     */
    RedisTaksBlockLock redisTaksBlockLock;

    /**
     *
     */
    RedisProperty redisProperty;

    /**
     *
     */

    AbstractMessageCenter messageCenter;

    /**
     * @param consumerManager 消费者管理器的实现¬
     */
    public RedisMessageReceiver(AbstractMessageCenter center, RedisMessageListenerContainer listenerContainer,
        RedisProperty property, RedisTaksBlockLock redisTaksBlockLock) {
        this.listenerContainer = listenerContainer;
        this.redisTaksBlockLock = redisTaksBlockLock;
        this.redisProperty = property;
        messageCenter = center;
    }

    /**
     *
     */
    @Deprecated
    @Override
    public IMessage receive() {
        throw new RuntimeException("no implement because RedisMessageReceiver will handle message auto");
    }

    @Override
    public boolean avaliablePull() {
        return true;
    }

    @Override
    public boolean avaliableReceive() {
        return false;
    }

    @Override
    public void pull(IMessage message) {
        if (message instanceof ConsumerWrappedMessage) {
            ConsumerWrappedMessage consumerWrappedMessage = (ConsumerWrappedMessage)message;
            messageCenter.consumeMessages(consumerWrappedMessage.consumer(), message);
        } else {
            messageCenter.consumeMessage(message);
        }
    }

    @Override
    public void destroy() throws Exception {
        listenerContainer.stop();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        for (IMessageConsumer consumer : messageCenter.allConsumersInCenter()) {
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
            RedisMessageConsumer redisMessageConsumer = new RedisMessageConsumer(consumer);
            MessageListenerAdapter adapter = new MessageListenerAdapter(redisMessageConsumer, "consume");
            adapter.setSerializer(new JdkSerializationRedisSerializer());
            listenerContainer.addMessageListener(adapter, topics);
            adapter.afterPropertiesSet();
        }
        listenerContainer.start();
    }

    @Data
    private class RedisMessageConsumer {

        /**
         *
         */
        private ILogger logger = null;
        /**
         *
         */
        private IMessageConsumer consumer;

        /**
         *
         */
        private ILogger getLogger() {
            if (logger == null) {
                logger = LogUtil.getInstance().getLogger(this.getClass().getName());
            }
            return logger;
        }

        /**
         *
         */
        public RedisMessageConsumer(IMessageConsumer consumer) {
            this.consumer = consumer;
        }

        /**
         *
         */
        private static final String LOCK_KEY = "MESSAGE_CENTER_BLOCK_KEY";

        /**
         * @param message 信息
         */
        public void consume(IMessage message) {
            try {
                if (redisProperty.isMessageHandleOnce()) {
                    redisTaksBlockLock.lock(lockId(message));
                }
                if (avaliablePull()) {
                    pull(ConsumerWrappedMessage.of(message, consumer));
                }
            } catch (ObtainLockFailException r) {
                getLogger().warn("get lock to fail:{}", r.getMessage());
            } finally {
                redisTaksBlockLock.unlock(lockId(message));
            }
        }

        /**
         * @param message 信息
         */
        private @NotNull String lockId(IMessage message) {
            return LOCK_KEY + message.getMessageId() + consumer.consumerId();
        }
    }

}
