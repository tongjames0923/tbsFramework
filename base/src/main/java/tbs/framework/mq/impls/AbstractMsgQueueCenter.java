package tbs.framework.mq.impls;

import cn.hutool.core.collection.CollUtil;
import tbs.framework.base.lock.impls.SimpleLockAddtionalInfo;
import tbs.framework.base.proxy.impls.LockProxy;
import tbs.framework.mq.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * @author abstergo
 */
public abstract class AbstractMsgQueueCenter extends AbstractMessageCenter {

    ConcurrentHashMap<String, Map<String, IMessageConsumer>> consumers = new ConcurrentHashMap<>();

    private static final String LOCK_NAME = "SIMPLE_MSG_CENTER_LOCK";
    @Resource
    LockProxy lockProxy;

    @Resource
    List<IMessageConsumer> consumerList;

    ExecutorService service = Executors.newFixedThreadPool(1);

    protected abstract QueueListener getQueueListener();

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

    @Override
    public void publish(IMessage message) {
        lockProxy.safeProxy((p) -> {
            super.publish(message);
            return null;
        }, null, new SimpleLockAddtionalInfo(LOCK_NAME));
    }

    @Override
    protected void sendMessage(IMessage message) {
        getQueueListener().getQueue().insert(message);
    }

    @Override
    public boolean onMessageReceived(IMessage message) {
        List<IMessageConsumer> consumers = selectMessageConsumer(message);
        if (CollUtil.isEmpty(consumers)) {
            throw new UnsupportedOperationException("none consumer found");
        }
        for (IMessageConsumer consumer : consumers) {
            int r = 0;
            try {
                consumer.consume(message);
            } catch (Exception e) {
                if (!onMessageFailed(message, r++, IMessageQueueEvents.MessageHandleType.Receive, e, consumer)) {
                    break;
                }
            }
        }
        return message.consumed();
    }

    @Override
    public List<IMessageConsumer> selectMessageConsumer(IMessage message) {
        List<IMessageConsumer> c = new LinkedList<>();
        return consumers.getOrDefault(message.getTopic(), new HashMap<>()).values().stream()
            .collect(Collectors.toList());
    }

    @Override
    public void centerStartToWork() {
        for (IMessageConsumer consumer : consumerList) {
            setMessageConsumer(consumer);
        }
        setStarted(true);
        service.execute(() -> {
            while (isStart()) {
                lockProxy.safeProxy((p) -> {
                    getQueueListener().listen(this);
                    return null;
                }, null, new SimpleLockAddtionalInfo(LOCK_NAME));
            }
        });

    }

    @Override
    public void centerStopToWork() {
        setStarted(false);
        service.shutdown();
    }
}
