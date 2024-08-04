package tbs.framework.auth.interfaces.impls.tokenPickers;

import cn.hutool.core.util.StrUtil;
import tbs.framework.auth.interfaces.IRequestTokenPicker;
import tbs.framework.auth.model.TokenModel;
import tbs.framework.auth.properties.AuthProperty;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedList;
import java.util.List;

/**
 * @author abstergo
 */
public class ParameterRequestTokenPicker implements IRequestTokenPicker {
    @Resource
    AuthProperty authProperty;

    @Override
    public List<TokenModel> getToken(final HttpServletRequest request, final HttpServletResponse response) {
        List<TokenModel> tokenModels = new LinkedList<>();
        for (String tokenField : authProperty.getTokenFields()) {
            String v = request.getParameter(tokenField);
            if (StrUtil.isNotEmpty(v)) {
                tokenModels.add(new TokenModel(tokenField, v, request));
            }
        }
        return tokenModels;
    }

    @Override
    public List<String> paths() {
        return authProperty.getAuthPathPattern();
    }
}
