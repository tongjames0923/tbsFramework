package tbs.framework.zookeeper.interfaces;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

public interface IZookeeperListenner {
    boolean accept(String path, Watcher.Event.KeeperState state, Watcher.Event.EventType type);

    void onEvent(Watcher watcher, WatchedEvent event);

}
