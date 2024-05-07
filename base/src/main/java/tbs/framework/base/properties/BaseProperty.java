package tbs.framework.base.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import tbs.framework.base.proxy.IProxy;
import tbs.framework.base.utils.LogUtil;
import tbs.framework.base.utils.UuidUtil;

/**
 * @author Abstergo
 */
@Data
@ConfigurationProperties(prefix = "tbs.framework.base")
public class BaseProperty {
    /**
     * 日志工具
     */
    private Class<? extends LogUtil> loggerProvider;

    /**
     * uuid工具
     */
    private Class<? extends UuidUtil> uuidProvider;

    /**
     * 异常代理
     */
    private Class<? extends IProxy> errorProxy;
}
