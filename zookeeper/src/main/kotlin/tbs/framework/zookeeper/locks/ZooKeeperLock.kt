package tbs.framework.zookeeper.locks

import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.recipes.locks.InterProcessMutex
import tbs.framework.lock.ILock
import java.time.Duration

class ZooKeeperLock : ILock {

    val interProcessMutex: InterProcessMutex;

    constructor(client: CuratorFramework, path: String) {
        interProcessMutex = InterProcessMutex(client, path);
    }

    override fun tryLock(timeOut: Duration?): Boolean {
        return interProcessMutex.acquire(timeOut?.toMillis() ?: 1000, java.util.concurrent.TimeUnit.MILLISECONDS);
    }

    override fun isHeldByCurrentThread(): Boolean {
        return interProcessMutex.isOwnedByCurrentThread
    }

    override fun isLocked(): Boolean {
        return interProcessMutex.isAcquiredInThisProcess
    }

    override fun unLock() {
        interProcessMutex.release()
    }
}