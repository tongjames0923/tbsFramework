package tbs.framework.redis.impls.mq.receiver;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import tbs.framework.mq.center.AbstractMessageCenter;
import tbs.framework.mq.connector.IMessageConnector;
import tbs.framework.mq.receiver.IMessageReceiver;
import tbs.framework.mq.receiver.impls.AbstractIdentityReceiver;
import tbs.framework.mq.sender.IMessagePublisher;
import tbs.framework.redis.impls.mq.sender.RedisSender;
import tbs.framework.redis.properties.RedisMqProperty;
import tbs.framework.utils.BeanUtil;
import tbs.framework.utils.IStartup;

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

    @Resource
    private RedisMessageListenerContainer container;


    @Resource
    @Lazy
    RedisMqProperty redisMqProperty;

    @Resource
    RedisSender sender;

    public RedisMessageConnector() {
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

    @Override
    public void createPublishers(AbstractMessageCenter center) {
        center.setMessagePublisher(this.sender);
    }

    @Override
    public void createReceivers(AbstractMessageCenter center) {
        List<IMessageReceiver> receivers = new LinkedList<>();
        center.allConsumersInCenter().forEach((c) -> {
            RedisChannelReceiver receiver = new RedisChannelReceiver(c);
            if (SpringUtil.getApplicationContext().containsBean(receiver.receiverId() + c.consumerId())) {
                return;
            }
            BeanUtil.registerBean(receiver, receiver.receiverId() + c.consumerId());
            center.addReceivers(receiver);
            receivers.add(receiver);
        });
        setUpListenners(receivers);
    }

    @Override
    public void destoryPublishers(AbstractMessageCenter center, List<IMessagePublisher> publishers) {

    }

    @Override
    public void destoryReceivers(AbstractMessageCenter center, List<IMessageReceiver> receivers) {
        for (IMessageReceiver receiver : receivers) {
            if (receiver instanceof AbstractIdentityReceiver) {
                AbstractIdentityReceiver abstractIdentityReceiver = (AbstractIdentityReceiver)receiver;
                abstractIdentityReceiver.setAvaliable(false);
                MessageListenerAdapter adapter = listeners.get(((AbstractIdentityReceiver)receiver).receiverId());
                container.removeMessageListener(adapter);
            }
            SpringUtil.getApplicationContext().getAutowireCapableBeanFactory().destroyBean(receiver);
        }
    }
}
