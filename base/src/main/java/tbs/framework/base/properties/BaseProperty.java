package tbs.framework.base.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Data
@ConfigurationProperties(prefix = "tbs.framework.base")
public class BaseProperty {
    private String loggerProvider;
    private String errorProxy;
}
