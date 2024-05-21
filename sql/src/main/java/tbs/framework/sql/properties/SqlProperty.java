package tbs.framework.sql.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import tbs.framework.sql.interfaces.ISqlLogger;

import java.util.List;

/**
 * @author abstergo
 */
@Data
@ConfigurationProperties("tbs.framework.sql")
public class SqlProperty {
    private boolean enableLogInterceptor;
}
