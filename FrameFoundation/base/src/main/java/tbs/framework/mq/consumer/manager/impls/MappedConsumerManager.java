package tbs.framework.mq.consumer.manager.impls;

import cn.hutool.core.collection.CollUtil;
import tbs.framework.mq.consumer.IMessageConsumer;
import tbs.framework.mq.consumer.manager.IMessageConsumerManager;
import tbs.framework.mq.message.IMessage;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static tbs.framework.mq.center.AbstractMessageCenter.checkInputConsumer;

/**
 * 完全匹配管理器
 *
 * @author abstergo
 */
public class MappedConsumerManager implements IMessageConsumerManager {

    ConcurrentHashMap<String, Map<String, IMessageConsumer>> consumers = new ConcurrentHashMap<>();

    @Override
    public List<IMessageConsumer> selectMessageConsumer(IMessage message) {
        List<IMessageConsumer> c = new LinkedList<>();
        return consumers.getOrDefault(message.getTopic(), new HashMap<>()).values().stream()
            .collect(Collectors.toList());
    }

    @Override
    public boolean match(String topic, Set<String> acceptTopics) {
        return acceptTopics.contains(topic);
    }

    @Override
    public List<IMessageConsumer> getConsumers() {
        List<IMessageConsumer> c = new LinkedList<>();
        consumers.values().stream().map((v) -> {
            return v.values();
        }).forEach((p) -> {
            c.addAll(p);
        });
        return c.stream().filter(p -> p != null).collect(Collectors.toList());
    }

    @Override
    public IMessageConsumerManager setMessageConsumer(IMessageConsumer messageConsumer) {
        checkInputConsumer(messageConsumer);
        Set<String> keys = messageConsumer.avaliableTopics();
        if (CollUtil.isEmpty(keys)) {
            return this;
        }
        for (String key : keys) {
            Map<String, IMessageConsumer> messageConsumers = consumers.getOrDefault(key, new HashMap<>());
            messageConsumers.put(messageConsumer.consumerId(), messageConsumer);
            consumers.put(key, messageConsumers);
        }
        return this;
    }

    @Override
    public boolean removeMessageConsumer(IMessageConsumer messageConsumer) {
        checkInputConsumer(messageConsumer);
        Set<String> keys = messageConsumer.avaliableTopics();
        if (CollUtil.isEmpty(keys)) {
            return false;
        }
        for (String key : keys) {
            Map<String, IMessageConsumer> messageConsumers = consumers.getOrDefault(key, new HashMap<>());
            messageConsumers.remove(messageConsumer.consumerId());
            consumers.put(key, messageConsumers);
        }
        return true;
    }
}
