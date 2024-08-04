package tbs.framework.sql.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author abstergo
 */
@Data
@ConfigurationProperties("tbs.framework.sql")
public class SqlProperty {
    private boolean enableLogInterceptor = false;
    private boolean enableAutoFillValueInterceptor = false;
}
