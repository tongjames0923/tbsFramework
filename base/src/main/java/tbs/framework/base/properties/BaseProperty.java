package tbs.framework.base.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import tbs.framework.base.log.ILogProvider;
import tbs.framework.base.proxy.IProxy;


@Data
@ConfigurationProperties(prefix = "tbs.framework.base")
public class BaseProperty {
    private Class<? extends ILogProvider> loggerProvider;
    private Class<? extends IProxy> errorProxy;
}
