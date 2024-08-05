package tbs.framework.auth.interfaces.token.impls.pickers;

import cn.hutool.core.util.StrUtil;
import tbs.framework.auth.interfaces.token.IRequestTokenPicker;
import tbs.framework.auth.model.TokenModel;
import tbs.framework.auth.properties.AuthProperty;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class HeaderRequestTokenPicker implements IRequestTokenPicker {
    @Resource
    private AuthProperty authProperty;

    @Override
    public List<TokenModel> getToken(final HttpServletRequest request, final HttpServletResponse response) {
        List<TokenModel> tokenModels = new LinkedList<>();
        for (String field : authProperty.getTokenFields().stream().collect(Collectors.toSet())) {
            final String token = request.getHeader(field);
            if (StrUtil.isNotEmpty(token)) {
                tokenModels.add(new TokenModel(field, token, request));
            }

        }
        Set<String> unforcedTokenFields = authProperty.getUnForcedTokenFields().stream().collect(Collectors.toSet());
        for (String field : unforcedTokenFields) {
            final String token = request.getHeader(field);
            if (StrUtil.isNotEmpty(token)) {
                TokenModel model = new TokenModel(field, token, request);
                model.setForceCheck(false);
                tokenModels.add(model);
            }
        }
        return tokenModels;
    }

    @Override
    public List<String> paths() {
        return authProperty.getAuthPathPattern();
    }
}
