/**
 * TokenModel 类用于表示令牌（Token）模型。
 */
package tbs.framework.auth.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.servlet.http.HttpServletRequest;

/**
 * TokenModel 类用于表示令牌（Token）模型。
 * @author abstergo
 */
@Data
@NoArgsConstructor
public class TokenModel {

    /**
     * 字段名
     */
    private String field;

    /**
     * 令牌值
     */
    private String token;

    /**
     * HttpServletRequest 对象
     */
    private HttpServletRequest request;

    /**
     * 是否强制检查
     */
    private boolean forceCheck = true;

    /**
     * 构造函数。
     *
     * @param field   字段名
     * @param token   令牌值
     * @param request HttpServletRequest 对象
     */
    public TokenModel(String field, String token, HttpServletRequest request) {
        this.field = field;
        this.token = token;
        this.request = request;
    }

    /**
     * 重写 toString 方法。
     *
     * @return 令牌模型字符串表示
     */
    @Override
    public String toString() {
        return "TokenModel{" +
            "field='" +
            field +
            '\'' +
            ", token='" +
            token +
            '\'' +
            ", forceCheck=" +
            forceCheck +
            '}';
    }
}

