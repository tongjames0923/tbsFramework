package tbs.framework.mq.consumer.manager.impls;

import cn.hutool.core.util.StrUtil;
import tbs.framework.base.log.ILogger;
import tbs.framework.base.utils.LogUtil;
import tbs.framework.mq.center.AbstractMessageCenter;
import tbs.framework.mq.message.IMessage;
import tbs.framework.mq.consumer.IMessageConsumer;
import tbs.framework.mq.consumer.manager.IMessageConsumerManager;

import java.util.LinkedList;
import java.util.List;
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
            logger = LogUtil.getInstance().getLogger(PatternConsumerManager.class.getName());
        }
        return logger;
    }

    @Override
    public List<IMessageConsumer> getConsumers() {
        return consumers;
    }

    @Override
    public IMessageConsumerManager setMessageConsumer(IMessageConsumer messageConsumer) {
        AbstractMessageCenter.checkInputConsumer(messageConsumer);
        consumers.add(messageConsumer);
        return this;
    }

    @Override
    public boolean removeMessageConsumer(IMessageConsumer messageConsumer) {
        AbstractMessageCenter.checkInputConsumer(messageConsumer);
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
