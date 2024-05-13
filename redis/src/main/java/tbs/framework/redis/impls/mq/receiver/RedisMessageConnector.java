package tbs.framework.redis.impls.mq.receiver;

import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import tbs.framework.base.utils.IStartup;
import tbs.framework.mq.connector.IMessageConnector;
import tbs.framework.mq.consumer.IMessageConsumer;
import tbs.framework.mq.receiver.IMessageReceiver;
import tbs.framework.redis.impls.lock.RedisTaksBlockLock;
import tbs.framework.redis.impls.mq.RedisMessageCenter;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author Abstergo
 */
public class RedisMessageConnector implements IStartup, DisposableBean, IMessageConnector {
    /**
     *
     */
    public static final String TOPIC_PREFIX = "MESSAGE_CENTER.";

    @Override
    public int getOrder() {
        return 1;
    }

    private RedisMessageListenerContainer container;

    private boolean isHandleOnce;
    private RedisTaksBlockLock taksBlockLock;

    @Resource
    @Lazy
    RedisMessageCenter center;

    public RedisMessageConnector(RedisMessageListenerContainer container, boolean isHandleOnce,
        RedisTaksBlockLock taksBlockLock) {
        this.container = container;
        this.isHandleOnce = isHandleOnce;
        this.taksBlockLock = taksBlockLock;
    }

    @Override
    public void factoryMessageReceivers(List<IMessageReceiver> receivers) {
        if (receivers == null) {
            throw new NullPointerException("receivers is null");
        }
        for (IMessageConsumer consumer : center.allConsumersInCenter()) {
            receivers.add(new RedisChannelReceiver(center, consumer, isHandleOnce, taksBlockLock, this));
        }
    }

    private void setup() {

        for (IMessageReceiver messageReceiver : center.getReceivers()) {
            if (messageReceiver == null) {
                continue;
            }
            MessageListenerAdapter adapter = new MessageListenerAdapter(messageReceiver, "pull");
            adapter.setSerializer(new JdkSerializationRedisSerializer());

            List<PatternTopic> topics = getTopic(messageReceiver.acceptTopics());
            container.addMessageListener(adapter, topics);
            adapter.afterPropertiesSet();
        }
        container.start();
    }

    private static List<PatternTopic> getTopic(Set<String> inputs) {
        List<PatternTopic> topics = new LinkedList<>();
        for (String i : inputs) {
            if (StrUtil.isEmpty(i)) {
                continue;
            }
            topics.add(new PatternTopic(TOPIC_PREFIX + "*" + i));
        }
        return topics;
    }

    @Override
    public void destroy() throws Exception {
        container.stop();
    }

    @Override
    public void startUp() throws RuntimeException {
        setup();
    }
}
