package tbs.framework.auth.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.servlet.http.HttpServletRequest;

/**
 * @author abstergo
 */
@Data
@NoArgsConstructor
public class TokenModel {
    private String field;
    private String token;
    private HttpServletRequest request;
    private boolean forceCheck = true;

    public TokenModel(String field, String token, HttpServletRequest request) {
        this.field = field;
        this.token = token;
        this.request = request;
    }

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
