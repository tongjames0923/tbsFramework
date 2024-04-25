package tbs.framework.auth.exceptions;

/**
 * 用户数据未查到异常
 * @author abstergo
 */
public class UserModelNotFoundException extends RuntimeException{
    public UserModelNotFoundException(String message) {
        super(message);
    }
}
