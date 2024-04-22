package tbs.framework.base.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "tbs.framework.local")
@Data
public class LocalProperty {
    public static enum LocalType {
        Cookie, Header, Parameter
    }

    private LocalType type = LocalType.Header;
    private String value = "lang";

}
