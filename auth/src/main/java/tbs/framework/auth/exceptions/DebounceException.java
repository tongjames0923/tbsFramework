package tbs.framework.auth.exceptions;

public class DebounceException extends RuntimeException{
    public DebounceException(String message) {
        super(message);
    }
}
