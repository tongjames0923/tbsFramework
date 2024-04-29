package tbs.framework.auth.interfaces.impls.tokenPickers;

import tbs.framework.auth.interfaces.IRequestTokenPicker;
import tbs.framework.auth.properties.AuthProperty;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HeaderRequestTokenPicker implements IRequestTokenPicker {
    @Resource
    private AuthProperty authProperty;

    @Override
    public String getToken(final HttpServletRequest request, final HttpServletResponse response) {
        final String token = request.getHeader(this.authProperty.getTokenField());
        return token;
    }
}
