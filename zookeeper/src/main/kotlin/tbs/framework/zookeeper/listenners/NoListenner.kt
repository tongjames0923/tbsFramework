package tbs.framework.zookeeper.listenners

import org.apache.zookeeper.WatchedEvent
import org.apache.zookeeper.Watcher
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.stereotype.Component
import tbs.framework.zookeeper.interfaces.IZookeeperListenner

@Component
@ConditionalOnMissingBean(IZookeeperListenner::class)
class NoListenner : IZookeeperListenner {
    override fun accept(path: String?, state: Watcher.Event.KeeperState?, type: Watcher.Event.EventType?): Boolean {
        return false
    }

    override fun onEvent(watcher: Watcher?, event: WatchedEvent?) {

    }
}