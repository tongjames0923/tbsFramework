package tbs.framework.auth.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import tbs.framework.auth.interfaces.IRequestTokenPicker;
import tbs.framework.auth.interfaces.impls.tokenPickers.HeaderRequestTokenPicker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "tbs.framework.auth")
public class AuthProperty {
    /**
     * 请求密钥提取
     */
    private Class<? extends IRequestTokenPicker> tokenPicker = HeaderRequestTokenPicker.class;

    /**
     * 获取用户请求token的字段
     */
    private String userModelTokenField = "token";

    /**
     * 必要密钥位置，默认值仅包含用户请求token的字段
     */
    private List<String> tokenFields = new ArrayList<>(Collections.singleton(userModelTokenField));

    /**
     * 非必要密钥位置，当检查器处理请求中非必要密钥时，不会抛出异常。
     */
    private List<String> unForcedTokenFields = new ArrayList<>();



    /**
     * 是否启动跨域
     */
    private boolean enableCors = true;
    /**
     * 权限功能生效路径匹配
     */
    private List<String> authPathPattern;

    /**
     * 是否启动默认的注解权限验证器
     */
    private boolean enableAnnotationPermissionValidator = true;

}
