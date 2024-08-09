package tbs.framework.auth.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Arrays;
import java.util.List;

/**
 * @author abstergo
 */
@Data
@ConfigurationProperties(prefix = "tbs.framework.auth.debounce")
public class DebounceProperty {

    /**
     * 接口防抖动时间，单位毫秒
     */
    private int apiColdDownTime = 1000;

    /**
     * 防抖动路径
     */
    private List<String> debouncePathPattern = Arrays.asList("/**");
}
