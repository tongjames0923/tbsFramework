package tbs.framework.mq.impls.consumer.manager;

import cn.hutool.core.collection.CollUtil;
import tbs.framework.mq.IMessage;
import tbs.framework.mq.IMessageConsumer;
import tbs.framework.mq.IMessageConsumerManager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static tbs.framework.mq.AbstractMessageCenter.checkInputConsumer;

/**
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
