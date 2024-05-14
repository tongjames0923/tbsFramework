package tbs.framework.lock;

import java.util.concurrent.TimeUnit;

/**
 * 锁
 * @author abstergo
 */
public interface ILock {

    /**
     * 尝试上锁
     * @param time 等待时间
     * @param unit 时间单位
     * @param lockId 锁id
     * @return true 获得锁，false获得锁失败
     * @throws InterruptedException
     */
    boolean tryLock(long time, TimeUnit unit, String lockId) throws InterruptedException;

    /**
     * 上锁
     * @param lockId 锁id
     */
    void lock(String lockId);

    /**
     * 锁是否上锁
     * @param lockId 锁id
     * @return true 已上锁 false 未上锁
     */
    boolean isLocked(String lockId);

    /**
     * 解锁
     * @param lockId 锁id
     */
    void unlock(String lockId);

}
