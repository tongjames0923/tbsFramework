package tbs.framework.auth.interfaces;

import org.springframework.core.Ordered;
import tbs.framework.auth.model.TokenModel;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;

/**
 * 请求token获取
 *
 * @author abstergo
 */
public interface IRequestTokenPicker extends Ordered {
    /**
     * 获取token
     *
     * @param request  请求
     * @param response 响应
     * @return token
     */
    List<TokenModel> getToken(HttpServletRequest request, HttpServletResponse response);

    public default List<String> paths() {
        return Collections.singletonList("/*");
    }

    @Override
    public default int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
