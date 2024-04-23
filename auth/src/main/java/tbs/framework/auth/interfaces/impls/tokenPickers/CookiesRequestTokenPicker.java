package tbs.framework.auth.interfaces.impls.tokenPickers;

import tbs.framework.auth.interfaces.IRequestTokenPicker;
import tbs.framework.auth.properties.AuthProperty;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * @author abstergo
 */
public class CookiesRequestTokenPicker implements IRequestTokenPicker {

    @Resource
    private AuthProperty authProperty;

    @Override
    public String getToken(HttpServletRequest request, HttpServletResponse response) {
        String val = null;
        for (Cookie c : request.getCookies()) {
            if (Objects.equals(c.getName(), authProperty.getTokenField())) {
                val = c.getValue();
                break;
            }
        }
        return val;
    }
}
