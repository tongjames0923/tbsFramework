package tbs.framework.auth.interfaces.token.impls.pickers;

import cn.hutool.core.util.StrUtil;
import tbs.framework.auth.interfaces.token.IRequestTokenPicker;
import tbs.framework.auth.model.TokenModel;
import tbs.framework.auth.properties.AuthProperty;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author abstergo
 */
public class CookiesRequestTokenPicker implements IRequestTokenPicker {

    @Resource
    private AuthProperty authProperty;

    @Override
    public List<TokenModel> getToken(final HttpServletRequest request, final HttpServletResponse response) {
        List<TokenModel> tokenModels = new LinkedList<>();
        Set<String> tokenFields = authProperty.getTokenFields().stream().collect(Collectors.toSet());
        Set<String> unforcedTokenFields = authProperty.getUnForcedTokenFields().stream().collect(Collectors.toSet());
        for (final Cookie c : request.getCookies()) {
            for (final String tokenField : tokenFields) {
                if (Objects.equals(c.getName(), tokenField) && StrUtil.isNotEmpty(c.getValue())) {
                    tokenModels.add(new TokenModel(c.getName(), c.getValue(), request));
                    tokenFields.remove(tokenField);
                }
            }
            for (final String unforcedTokenField : unforcedTokenFields) {
                if (Objects.equals(c.getName(), unforcedTokenField) && StrUtil.isNotEmpty(c.getValue())) {
                    TokenModel model = new TokenModel(c.getName(), c.getValue(), request);
                    model.setForceCheck(false);
                    tokenModels.add(model);
                    unforcedTokenFields.remove(unforcedTokenField);
                }
            }
        }
        return tokenModels;
    }

    @Override
    public List<String> paths() {
        return authProperty.getAuthPathPattern();
    }
}
