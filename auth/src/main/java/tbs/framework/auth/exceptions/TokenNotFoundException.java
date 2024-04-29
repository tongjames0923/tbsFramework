package tbs.framework.auth.exceptions;


/**
 * Token未获取
 * @author abstergo
 */
public class TokenNotFoundException extends RuntimeException {
    public TokenNotFoundException(final String message) {
        super(message);
    }
}
