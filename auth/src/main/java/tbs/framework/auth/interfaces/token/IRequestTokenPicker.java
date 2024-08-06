/**
 * IRequestTokenPicker 接口用于从请求中获取令牌（Token）。
 */
package tbs.framework.auth.interfaces.token;

import org.springframework.core.Ordered;
import tbs.framework.auth.model.TokenModel;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;

/**
 * IRequestTokenPicker 接口用于从请求中获取令牌（Token）。
 */
public interface IRequestTokenPicker extends Ordered {

    /**
     * 从请求中获取令牌。
     *
     * @param request  HttpServletRequest 对象
     * @param response HttpServletResponse 对象
     * @return 令牌模型列表
     */
    List<TokenModel> getToken(HttpServletRequest request, HttpServletResponse response);

    /**
     * 获取拦截路径。
     *
     * @return 拦截路径列表
     */
    default List<String> paths() {
        return Collections.singletonList("/*");
    }

    @Override
    default int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}

