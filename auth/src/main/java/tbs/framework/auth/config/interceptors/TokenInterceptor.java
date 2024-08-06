package tbs.framework.auth.config.interceptors;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.web.servlet.HandlerInterceptor;
import tbs.framework.auth.exceptions.TokenNotFoundException;
import tbs.framework.auth.interfaces.token.IRequestTokenPicker;
import tbs.framework.auth.interfaces.token.ITokenParser;
import tbs.framework.auth.model.RuntimeData;
import tbs.framework.auth.model.TokenModel;
import tbs.framework.log.ILogger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * token拦截器
 * @author abstergo
 */
public class TokenInterceptor implements HandlerInterceptor {

    IRequestTokenPicker tokenPicker;

    ILogger logger;

    private List<ITokenParser> tokenParser;

    public TokenInterceptor(final IRequestTokenPicker tokenPicker, final List<ITokenParser> tokenParser,
        ILogger logger) {
        this.tokenPicker = tokenPicker;
        this.tokenParser = tokenParser;
        this.logger = logger;
        if (CollUtil.isEmpty(tokenParser)) {
            throw new RuntimeException("token解析器不能为空");
        }
    }

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler)
        throws Exception {
        List<TokenModel> tokenList = tokenPicker.getToken(request, response);
        if (CollUtil.isEmpty(tokenList)) {
            throw new TokenNotFoundException("Token尚未传递");
        }
        for (TokenModel model : tokenList) {
            if (RuntimeData.getInstance().getTokenList().contains(model)) {
                throw new RuntimeException("Token重复传递");
            }
            checkTokens(model);
            logger.debug("Token校验通过,{}", model);
        }
        RuntimeData.getInstance().getTokenList().addAll(tokenList);
        if (StrUtil.isEmpty(RuntimeData.getInstance().getInvokeUrl())) {
            RuntimeData.getInstance().setInvokeUrl(request.getRequestURI());
        }
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    private void checkTokens(TokenModel model) {
        boolean checked = false;
        for (ITokenParser parser : tokenParser) {
            if (parser.support(model.getField())) {
                try {
                    parser.parseToken(model, RuntimeData.getInstance());
                } catch (Exception e) {
                    if (model.isForceCheck()) {
                        throw e;
                    } else {
                        logger.warn("Token校验失败,", model);
                    }
                } finally {
                    checked = true;
                }
            } else {
                continue;
            }
        }
        if (!checked && model.isForceCheck()) {
            throw new RuntimeException("Token解析器不支持的Token[" + model.toString() + "]");
        }
    }
}
