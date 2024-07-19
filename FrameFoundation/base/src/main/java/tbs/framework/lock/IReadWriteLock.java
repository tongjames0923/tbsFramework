package tbs.framework.lock;

/**
 * IReadWriteLock 接口定义了读写锁的基本操作。
 *
 * @author Abstergo
 */
public interface IReadWriteLock {
    ILock readLock();

    ILock writeLock();
}

