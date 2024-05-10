package tbs.framework.redis.impls;

import org.springframework.data.redis.core.RedisTemplate;
import tbs.framework.base.log.ILogger;
import tbs.framework.base.utils.LogUtil;
import tbs.framework.mq.IMessage;
import tbs.framework.mq.IMessageQueue;
import tbs.framework.mq.impls.AbstractMsgQueueCenter;
import tbs.framework.mq.impls.QueueListener;

import javax.annotation.Resource;

/**
 * @author abstergo
 */
public abstract class AbstractRedisMessageCenter extends AbstractMsgQueueCenter {

    private RedisMessageReceiver receiver;

    private QueueListener queueListener;
    private ILogger logger = null;

    @Resource(name = "REDIS_MSG")
    private RedisTemplate<String, Object> redisTemplate;

    public AbstractRedisMessageCenter(RedisMessageReceiver receiver) {
        this.receiver = receiver;
        this.queueListener = new QueueListener() {
            @Override
            protected IMessageQueue getQueue() {
                return receiver.messageQueue();
            }
        };
    }

    @Override
    public void centerStopToWork() {
        receiver.end();
        super.centerStopToWork();

    }

    @Override
    public void centerStartToWork() {
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
    protected QueueListener getQueueListener() {
        return queueListener;
    }
}
