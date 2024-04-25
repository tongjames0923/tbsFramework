package tbs.framework.base.properties;

import cn.hutool.http.Header;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "tbs.framework.locale")
@Data
public class LocalProperty {
    public enum LocalType {
        /**
         * 从Cookie获取
         */
        Cookie,
        /**
         * 从头部信息获取
         */
        Header,
        /**
         * 从请求参数获取
         */
        Parameter
    }

    /**
     * 读取本地化信息的位置
     */
    private LocalType type = LocalType.Header;
    /**
     * 读取本地化信息的key
     */
    private String value = "lang";

    /**
     * 本地化路径匹配
     */
    private String pathPattern = "/*";

}
