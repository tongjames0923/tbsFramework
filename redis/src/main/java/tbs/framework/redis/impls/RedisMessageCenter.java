package tbs.framework.redis.impls;

import org.springframework.data.redis.core.RedisTemplate;
import tbs.framework.base.log.ILogger;
import tbs.framework.base.utils.LogUtil;
import tbs.framework.mq.IMessage;
import tbs.framework.mq.IMessageConsumerManager;
import tbs.framework.mq.IMessageQueue;
import tbs.framework.mq.IMessageQueueEvents;
import tbs.framework.mq.impls.center.AbstractMsgQueueCenter;
import tbs.framework.mq.impls.listener.BaseQueueListener;

import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;

/**
 * @author abstergo
 */
public class RedisMessageCenter extends AbstractMsgQueueCenter {

    private RedisMessageReceiver receiver;

    private BaseQueueListener baseQueueListener;
    private ILogger logger = null;

    @Resource(name = "REDIS_MSG")
    private RedisTemplate<String, Object> redisTemplate;

    public RedisMessageCenter(RedisMessageReceiver receiver, IMessageConsumerManager cm, IMessageQueueEvents qe,
        ExecutorService es) {
        super(cm, qe, es);
        this.receiver = receiver;
        this.baseQueueListener = new BaseQueueListener() {
            @Override
            public IMessageQueue getQueue() {
                return receiver.messageQueue();
            }
        };
    }

    @Override
    protected void centerStopToWork() {
        receiver.end();
        super.centerStopToWork();
    }

    @Override
    protected void centerStartToWork() {
        receiver.begin();
        super.centerStartToWork();
    }

    @Override
    protected void sendMessage(IMessage message) {
        redisTemplate.convertAndSend(RedisMessageReceiver.TOPIC_PREFIX + message.getTopic(), message);
    }

    private ILogger getLogger() {
        if (logger == null) {
            logger = LogUtil.getInstance().getLogger(this.getClass().getName());
        }
        return logger;
    }

    @Override
    protected BaseQueueListener getQueueListener() {
        return baseQueueListener;
    }

    @Override
    protected long listenSpan() {
        return baseQueueListener.getQueue().isEmpty() ? 50 : 0;
    }
}
