package tbs.framework.mq.impls;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import tbs.framework.base.lock.impls.SimpleLockAddtionalInfo;
import tbs.framework.base.proxy.impls.LockProxy;
import tbs.framework.mq.AbstractMessageCenter;
import tbs.framework.mq.IMessage;
import tbs.framework.mq.IMessageConsumer;
import tbs.framework.mq.IMessageQueue;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @author abstergo
 */
public abstract class AbstractSimpleMsgCenter extends AbstractMessageCenter {

    ConcurrentHashMap<String, Map<String, IMessageConsumer>> consumers = new ConcurrentHashMap<>();
    AtomicBoolean beginReciving = new AtomicBoolean(false);

    private static final String LOCK_NAME = "SIMPLE_MSG_CENTER_LOCK";
    @Resource
    LockProxy lockProxy;

    ExecutorService service = Executors.newFixedThreadPool(1);

    protected abstract IMessageQueue getMessageQueue();



    @Override
    public AbstractMessageCenter setMessageConsumer(IMessageConsumer messageConsumer) {
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
        if (!isStart()) {
            centerStartToWork();
        }
        lockProxy.safeProxy((p) -> {
            super.publish(message);
            return null;
        }, null, new SimpleLockAddtionalInfo(LOCK_NAME));
    }

    @Override
    protected void sendMessage(IMessage message) {
        getMessageQueue().insert(message);
    }

    @Override
    protected boolean onMessageReceived(IMessage message) {
        List<IMessageConsumer> consumers = selectMessageConsumer(message);
        if (CollUtil.isEmpty(consumers)) {
            throw new UnsupportedOperationException("none consumer found");
        }
        for (IMessageConsumer consumer : consumers) {
            int r = 0;
            try {
                consumer.consume(message);
            } catch (Exception e) {
                onMessageFailed(message, r++, MessageHandleType.Receive, e, consumer);
            }
        }
        return message.consumed();
    }

    @Override
    protected List<IMessageConsumer> selectMessageConsumer(IMessage message) {
        List<IMessageConsumer> c = new LinkedList<>();
        return consumers.getOrDefault(message.getTopic(), new HashMap<>()).values().stream()
            .collect(Collectors.toList());
    }

    @Override
    public void centerStartToWork() {
        beginReciving.set(true);
        service.execute(() -> {
            while (beginReciving.get()) {
                lockProxy.safeProxy((p) -> {
                    if (getMessageQueue().isEmpty()) {
                        Thread.yield();
                        return null;
                    }
                    IMessage message = getMessageQueue().getNext();
                    if (message == null) {
                        Thread.yield();
                        return null;
                    }
                    if (!onMessageReceived(message)) {
                        getMessageQueue().insert(message);
                    }
                    return null;
                }, null, new SimpleLockAddtionalInfo(LOCK_NAME));
            }
        });

    }

    @Override
    public void centerStopToWork() {
        beginReciving.set(false);
        service.shutdown();
    }

    @Override
    public boolean isStart() {
        return beginReciving.get();
    }
}
