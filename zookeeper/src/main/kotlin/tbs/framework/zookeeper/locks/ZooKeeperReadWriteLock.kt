package tbs.framework.zookeeper.locks

import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock
import tbs.framework.lock.ILock
import tbs.framework.lock.IReadWriteLock

class ZooKeeperReadWriteLock : IReadWriteLock {
    var interProcessReadWriteLock: InterProcessReadWriteLock;

    constructor(framework: CuratorFramework, path: String = "/locks/read_write_locks/") {
        interProcessReadWriteLock = InterProcessReadWriteLock(framework, path);
    }


    override fun readLock(): ILock {
        return ZooKeeperLockAdapter(interProcessReadWriteLock.readLock())
    }

    override fun writeLock(): ILock {
        return ZooKeeperLockAdapter(interProcessReadWriteLock.writeLock())
    }
}