package tbs.framework.zookeeper.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Abstergo
 */
@Data
@ConfigurationProperties(prefix = "tbs.framework.zookeeper")
public class ZooKeeperProperty {
    private String registryAddress;
    private String topNode = "/ZK_NODE";
    private int sessionTimeout = 3000;
    private int connectionTimeout = 5000;

}
