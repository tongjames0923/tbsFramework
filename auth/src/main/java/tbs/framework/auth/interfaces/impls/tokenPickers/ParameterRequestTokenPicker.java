package tbs.framework.auth.interfaces.impls.tokenPickers;

import tbs.framework.auth.interfaces.IRequestTokenPicker;
import tbs.framework.auth.properties.AuthProperty;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author abstergo
 */
public class ParameterRequestTokenPicker implements IRequestTokenPicker {
    @Resource
    AuthProperty authProperty;

    @Override
    public String getToken(final HttpServletRequest request, final HttpServletResponse response) {
        return request.getParameter(this.authProperty.getTokenField());
    }
}
