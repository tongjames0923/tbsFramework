package tbs.framework.zookeeper.locks

import org.apache.curator.framework.recipes.locks.InterProcessLock
import tbs.framework.lock.ILock
import java.time.Duration
import java.util.concurrent.TimeUnit

class ZooKeeperLockAdapter : ILock {
    val lock: InterProcessLock;

    constructor(lock: InterProcessLock) {
        this.lock = lock
    }

    override fun tryLock(timeOut: Duration?): Boolean {
        if (timeOut == null) {
            lock.acquire()
            return true;
        } else {
            return lock.acquire(timeOut.toMillis(), TimeUnit.MILLISECONDS)
        }
    }

    override fun isHeldByCurrentThread(): Boolean {
        return lock.isAcquiredInThisProcess
    }

    override fun isLocked(): Boolean {
        return lock.isAcquiredInThisProcess
    }

    override fun unLock() {
        lock.release()
    }
}