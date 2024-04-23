package tbs.framework.auth.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import tbs.framework.auth.interfaces.IRequestTokenPicker;
import tbs.framework.auth.interfaces.impls.tokenPickers.HeaderRequestTokenPicker;

import java.util.ArrayList;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "tbs.framework.auth")
public class AuthProperty {
    private Class<? extends IRequestTokenPicker> tokenPicker = HeaderRequestTokenPicker.class;
    private String tokenField = "token";
    private boolean enableCors=false;
    private List<String> tokenPickUrlPatterns;
}
