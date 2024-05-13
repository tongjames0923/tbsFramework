package tbs.framework.redis.impls.mq.receiver;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import tbs.framework.base.utils.IStartup;
import tbs.framework.mq.connector.IMessageConnector;
import tbs.framework.mq.receiver.IMessageReceiver;
import tbs.framework.mq.receiver.impls.AbstractIdentityReceiver;
import tbs.framework.redis.impls.lock.RedisTaksBlockLock;
import tbs.framework.redis.impls.mq.RedisMessageCenter;
import tbs.framework.redis.properties.RedisMqProperty;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Abstergo
 */
public class RedisMessageConnector implements IStartup, DisposableBean, IMessageConnector {
    /**
     *
     */
    public static final String TOPIC_PREFIX = "MESSAGE_CENTER.";

    private Map<String, MessageListenerAdapter> listeners = new ConcurrentHashMap<>();

    @Override
    public int getOrder() {
        return 1;
    }

    private RedisMessageListenerContainer container;

    private boolean isHandleOnce;
    private RedisTaksBlockLock taksBlockLock;

    @Resource
    @Lazy
    RedisMqProperty redisMqProperty;

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
        setUpListenners(receivers);
    }

    @Override
    public void invalidateReceivers(List<IMessageReceiver> receivers) {
        for (IMessageReceiver receiver : receivers) {
            if (receiver instanceof AbstractIdentityReceiver) {
                ((AbstractIdentityReceiver)receiver).setAvaliable(false);
                MessageListenerAdapter adapter = listeners.get(((AbstractIdentityReceiver)receiver).receiverId());
                container.removeMessageListener(adapter);
            }
        }
    }

    private void setUpListenners(List<IMessageReceiver> receivers) {
        for (IMessageReceiver messageReceiver : receivers) {
            if (messageReceiver == null) {
                continue;
            }
            MessageListenerAdapter adapter = new MessageListenerAdapter(messageReceiver, "pull");
            adapter.setSerializer(SpringUtil.getBean(redisMqProperty.getMessageSerializerClass()));

            List<PatternTopic> topics = getTopic(messageReceiver.acceptTopics());
            container.addMessageListener(adapter, topics);
            adapter.afterPropertiesSet();
            if (messageReceiver instanceof AbstractIdentityReceiver) {
                AbstractIdentityReceiver abstractIdentityReceiver = (AbstractIdentityReceiver)messageReceiver;
                listeners.putIfAbsent(abstractIdentityReceiver.receiverId(), adapter);
            }
        }
    }

    private void setup() {
        if (!container.isRunning()) {
            container.start();
        }
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
