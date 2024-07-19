package tbs.framework.lock.impls;

import tbs.framework.lock.ILock;
import tbs.framework.lock.IReadWriteLock;

import java.util.concurrent.locks.ReadWriteLock;

/**
 * @author Abstergo
 */
public class ReadWriteLockAdapter implements IReadWriteLock {

    private ILock readLock;

    private ILock writeLock;

    public ReadWriteLockAdapter(ReadWriteLock readWriteLock) {
        this.readLock = new LockAdapter(readWriteLock.readLock());
        this.writeLock = new LockAdapter(readWriteLock.writeLock());
    }

    @Override
    public ILock readLock() {
        return readLock;
    }

    @Override
    public ILock writeLock() {
        return writeLock;
    }
}
