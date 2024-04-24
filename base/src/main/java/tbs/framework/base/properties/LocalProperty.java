package tbs.framework.base.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "tbs.framework.locale")
@Data
public class LocalProperty {
    public static enum LocalType {
        Cookie, Header, Parameter
    }

    /**
     * 读取本地化信息的位置
     */
    private LocalType type = LocalType.Header;
    /**
     * 读取本地化信息的key
     */
    private String value = "lang";

}
