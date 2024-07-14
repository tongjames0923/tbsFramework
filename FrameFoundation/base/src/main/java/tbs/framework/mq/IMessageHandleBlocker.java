package tbs.framework.mq;

import java.time.Duration;

/**
 * 消息处理阻塞器，用于锁定和解锁消息处理。
 */
public abstract class IMessageHandleBlocker {

    /**
     * 尝试锁定消息处理，如果锁定成功则返回 true，否则返回 false。
     *
     * @param id    消息的唯一标识符。
     * @param alive 锁定持续的时间。
     * @return 如果锁定成功，则返回 true；否则返回 false。
     */
    protected abstract boolean lock(String id, Duration alive);

    /**
     * 解锁消息处理，并设置延迟时间。
     *
     * @param id    消息的唯一标识符。
     * @param delay 解锁后的延迟时间。
     */
    protected abstract void unlock(String id, Duration delay);

    /**
     * 尝试获取消息处理锁，如果获取失败则抛出运行时异常。
     *
     * @param id       消息的唯一标识符。
     * @param maxAlive 锁定持续的最大时间。
     */
    public void takeLock(String id, Duration maxAlive) {
        if (!lock(id, maxAlive)) {
            throw new RuntimeException("消息正在被处理");
        }
    }

    /**
     * 释放消息处理锁，并设置延迟时间。
     *
     * @param id    消息的唯一标识符。
     * @param delay 解锁后的延迟时间。
     */
    public void unTakeLock(String id, Duration delay) {
        unlock(id, delay);
    }
}
