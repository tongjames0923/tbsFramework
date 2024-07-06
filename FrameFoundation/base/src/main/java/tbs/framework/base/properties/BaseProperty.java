package tbs.framework.base.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import tbs.framework.base.utils.LogFactory;
import tbs.framework.utils.UuidUtil;

/**
 * @author Abstergo
 */
@Data
@ConfigurationProperties(prefix = "tbs.framework.base")
public class BaseProperty {
    /**
     * 日志工具
     */
    private Class<? extends LogFactory> loggerProvider;

    /**
     * uuid工具
     */
    private Class<? extends UuidUtil> uuidProvider;

}
