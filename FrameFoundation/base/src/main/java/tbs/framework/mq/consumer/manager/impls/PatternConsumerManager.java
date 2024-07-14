package tbs.framework.mq.consumer.manager.impls;

import cn.hutool.core.util.StrUtil;
import tbs.framework.base.utils.LogFactory;
import tbs.framework.log.ILogger;
import tbs.framework.mq.center.AbstractListenableMessageCenter;
import tbs.framework.mq.consumer.IMessageConsumer;
import tbs.framework.mq.consumer.manager.IMessageConsumerManager;
import tbs.framework.mq.message.IMessage;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 使用正则表达式匹配的
 *
 * @author abstergo
 */
public class PatternConsumerManager implements IMessageConsumerManager {

    List<IMessageConsumer> consumers = new LinkedList<>();

    private ILogger logger;

    private ILogger getLogger() {
        if (logger == null) {
            logger = LogFactory.Companion.getInstance().getLogger(PatternConsumerManager.class.getName());
        }
        return logger;
    }

    @Override
    public boolean match(String topic, Set<String> acceptTopics) {
        for (String key : acceptTopics) {
            Pattern pattern = Pattern.compile(key);
            Matcher matcher = pattern.matcher(topic);
            if (matcher.matches()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<IMessageConsumer> getConsumers() {
        return consumers;
    }

    @Override
    public IMessageConsumerManager setMessageConsumer(IMessageConsumer messageConsumer) {
        AbstractListenableMessageCenter.checkInputConsumer(messageConsumer);
        consumers.add(messageConsumer);
        return this;
    }

    @Override
    public boolean removeMessageConsumer(IMessageConsumer messageConsumer) {
        AbstractListenableMessageCenter.checkInputConsumer(messageConsumer);
        return consumers.removeIf((p) -> {
            return p == null || p.consumerId().equals(messageConsumer.consumerId());
        });
    }

    @Override
    public List<IMessageConsumer> selectMessageConsumer(IMessage message) {
        if (message == null) {
            return new LinkedList<>();
        }
        if (StrUtil.isEmpty(message.getTopic())) {
            return new LinkedList<>();
        }
        return consumers.stream().filter((p) -> {
            boolean mapped = false;
            for (String k : p.avaliableTopics()) {
                try {
                    Pattern pattern = Pattern.compile(k);
                    if (pattern.matcher(message.getTopic()).matches()) {
                        mapped = true;
                        break;
                    }
                } catch (Exception e) {
                    getLogger().error(null, "pattern fail for {} by {} msg:{}", message.getTopic(), k, e.getMessage());
                }
            }
            return mapped;
        }).collect(Collectors.toList());
    }
}
