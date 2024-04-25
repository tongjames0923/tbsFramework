package tbs.framework.auth.exceptions;


/**
 * Token未获取
 * @author abstergo
 */
public class TokenNotFoundException extends RuntimeException {
    public TokenNotFoundException(String message) {
        super(message);
    }
}
