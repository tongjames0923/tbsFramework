package tbs.framework.auth.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import tbs.framework.auth.interfaces.IRequestTokenPicker;
import tbs.framework.auth.interfaces.impls.tokenPickers.HeaderRequestTokenPicker;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "tbs.framework.auth")
public class AuthProperty {
    /**
     * 请求密钥提取
     */
    private Class<? extends IRequestTokenPicker> tokenPicker = HeaderRequestTokenPicker.class;
    /**
     * 密钥位置
     */
    private String tokenField = "token";
    /**
     * 是否启动跨域
     */
    private boolean enableCors = true;
    /**
     * 密钥提取路径
     */
    private List<String> tokenPickUrlPatterns;

    private boolean enableAnnotationPermissionValidator = true;

}
