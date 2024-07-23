package tbs.framework.zookeeper.annotations;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import tbs.framework.zookeeper.config.ZooKeeperConfig;
import tbs.framework.zookeeper.config.properties.ZooKeeperProperty;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Abstergo
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(ZooKeeperConfig.class)
@ComponentScan({"tbs.framework.zookeeper"})
@EnableConfigurationProperties({ZooKeeperProperty.class})
public @interface EnableZooKeeper {
    AdviceMode mode() default AdviceMode.PROXY;
}
