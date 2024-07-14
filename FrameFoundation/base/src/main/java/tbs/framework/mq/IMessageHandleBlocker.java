package tbs.framework.mq;

import java.time.Duration;

public abstract class IMessageHandleBlocker {

    protected abstract boolean lock(String id, Duration alive);

    protected abstract void unlock(String id, Duration delay);

    public void takeLock(String id, Duration maxAlive) {
        if (!lock(id, maxAlive)) {
            throw new RuntimeException("消息正在被处理");
        }
    }

    public void unTakeLock(String id, Duration delay) {
        unlock(id, delay);
    }

}
