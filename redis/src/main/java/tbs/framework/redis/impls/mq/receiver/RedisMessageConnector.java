package tbs.framework.redis.impls.mq.receiver;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import tbs.framework.base.properties.MqProperty;
import tbs.framework.mq.AbstractMessageHandleBlocker;
import tbs.framework.mq.center.AbstractMessageCenter;
import tbs.framework.mq.connector.IMessageConnector;
import tbs.framework.mq.message.IMessage;
import tbs.framework.mq.receiver.IMessageReceiver;
import tbs.framework.mq.receiver.impls.AbstractIdentityReceiver;
import tbs.framework.mq.receiver.impls.DirectConsumeReceiver;
import tbs.framework.mq.sender.IMessagePublisher;
import tbs.framework.redis.impls.mq.sender.RedisSender;
import tbs.framework.redis.properties.RedisMqProperty;
import tbs.framework.utils.IStartup;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Redis消息连接器类。
 *
 * @author Abstergo
 */
public class RedisMessageConnector implements IStartup, DisposableBean, IMessageConnector {
    /**
     * 消息主题前缀。
     */
    public static final String TOPIC_PREFIX = "MESSAGE_CENTER.";

    /**
     * 存储消息监听器的映射。
     */
    private Map<String, MessageListenerAdapter> listeners = new ConcurrentHashMap<>();

    @Resource
    MqProperty property;

    @Override
    public int getOrder() {
        return 1;
    }

    /**
     * Redis消息监听容器。
     */
    @Resource
    private RedisMessageListenerContainer container;

    /**
     * Redis消息队列属性。
     */
    @Resource
    @Lazy
    RedisMqProperty redisMqProperty;

    /**
     * Redis消息发送器。
     */
    @Resource
    RedisSender sender;

    /**
     * Redis任务阻塞锁。
     */
    @Resource
    AbstractMessageHandleBlocker taskBlockLock;

    public void redisConsume(IMessage message) {

    }

    /**
     * 设置消息监听器。
     *
     * @param receivers 消息接收器列表。
     * @param center
     */
    private void setUpListenners(List<IMessageReceiver> messageReceivers, AbstractMessageCenter center) {
        for (IMessageReceiver receiver : messageReceivers) {
            if (receiver == null) {
                continue;
            }

            MessageListenerAdapter adapter = new MessageListenerAdapter(receiver, "consumeDirectly");
            adapter.setSerializer(SpringUtil.getBean(redisMqProperty.getMessageSerializerClass()));

            List<PatternTopic> topics = getTopic(receiver.acceptTopics());
            container.addMessageListener(adapter, topics);
            adapter.afterPropertiesSet();
            if (receiver instanceof AbstractIdentityReceiver) {
                AbstractIdentityReceiver abstractIdentityReceiver = (AbstractIdentityReceiver)receiver;
                listeners.putIfAbsent(abstractIdentityReceiver.receiverId(), adapter);
            }
        }
    }

    /**
     * 启动消息监听容器。
     */
    private void setup() {
        if (!container.isRunning()) {
            container.start();
        }
    }

    /**
     * 获取消息主题。
     *
     * @param inputs 消息主题输入。
     * @return 消息主题列表。
     */
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
            DirectConsumeReceiver receiver =
                new DirectConsumeReceiver(this, c.avaliableTopics(), center, property, taskBlockLock);
            if (SpringUtil.getApplicationContext().containsBean(receiver.receiverId() + c.consumerId())) {
                return;
            }
            center.addReceivers(receiver);
            receivers.add(receiver);
        });
        setUpListenners(receivers, center);
    }

    @Override
    public void destoryPublishers(AbstractMessageCenter center, IMessagePublisher publishers) {

    }

    @Override
    public void destoryReceivers(AbstractMessageCenter center, List<IMessageReceiver> receivers) {
        for (IMessageReceiver receiver : receivers) {
            if (receiver instanceof AbstractIdentityReceiver) {
                AbstractIdentityReceiver abstractIdentityReceiver = (AbstractIdentityReceiver)receiver;
                abstractIdentityReceiver.setAvaliable(false);
                MessageListenerAdapter adapter = listeners.get(((AbstractIdentityReceiver)receiver).receiverId());
                if (adapter == null) {
                    continue;
                }
                container.removeMessageListener(adapter);
            }
        }
    }
}
