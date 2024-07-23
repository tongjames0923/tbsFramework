package tbs.framework.zookeeper.locks

import org.apache.curator.framework.CuratorFramework
import tbs.framework.lock.ILock
import tbs.framework.lock.ILockProvider
import tbs.framework.zookeeper.config.properties.ZooKeeperProperty
import javax.annotation.Resource

class ZooKeeperLockProvider : ILockProvider {

    @Resource
    lateinit var curatorFramework: CuratorFramework;

    @Resource
    lateinit var zooKeeperProperty: ZooKeeperProperty;

    override fun getLocker(target: Any?): ILock {

        return ZooKeeperLock(curatorFramework, zooKeeperProperty.topNode + "/locks/" + target.toString())
    }
}