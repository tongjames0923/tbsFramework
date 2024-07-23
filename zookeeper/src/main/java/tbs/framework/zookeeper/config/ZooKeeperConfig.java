package tbs.framework.zookeeper.config;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import tbs.framework.zookeeper.config.properties.ZooKeeperProperty;
import tbs.framework.zookeeper.interfaces.IZookeeperListenner;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Abstergo
 */
@Configuration
public class ZooKeeperConfig {

    @Resource
    ZooKeeperProperty property;

    @Bean
    @ConditionalOnMissingBean(Watcher.class)
    Watcher deafultWatcher() {
        return new Watcher() {
            @Resource
            @Lazy
            List<IZookeeperListenner> listenners;

            @Override
            public void process(WatchedEvent watchedEvent) {
                for (IZookeeperListenner listenner : listenners) {
                    if (listenner == null) {
                        continue;
                    }
                    if (listenner.accept(watchedEvent.getPath(), watchedEvent.getState(), watchedEvent.getType())) {
                        listenner.onEvent(this, watchedEvent);
                    }
                }
            }
        };
    }

    @Bean
    @ConditionalOnMissingBean(ZooKeeper.class)
    public CuratorFramework keeper(Watcher watcher) throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client =
            CuratorFrameworkFactory.newClient(property.getRegistryAddress(), property.getSessionTimeout(),
                property.getConnectionTimeout(), retryPolicy);
        client.start();
        String path = property.getTopNode().startsWith("/") ? property.getTopNode() : "/" + property.getTopNode();

        if (client.checkExists().forPath(path) == null) {
            client.create().creatingParentContainersIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
        }
        client.getData().usingWatcher(watcher).forPath(path);

        return client;

    }
}
