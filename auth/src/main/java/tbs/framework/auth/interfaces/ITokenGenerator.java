package tbs.framework.auth.interfaces;

/**
 * @author abstergo
 */
public interface ITokenGenerator<T> {
    /**
     * Generate a token
     *
     * @return
     */
    String generateToken(T tokenFactor) throws Exception;
}
