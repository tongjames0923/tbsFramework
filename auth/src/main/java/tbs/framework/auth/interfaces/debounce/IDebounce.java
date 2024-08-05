package tbs.framework.auth.interfaces.debounce;

import tbs.framework.auth.exceptions.DebounceException;
import tbs.framework.auth.model.UserModel;

import java.lang.reflect.Method;

public interface IDebounce {
    void debounce(String url, UserModel user, Method method, Object target, Object[] args) throws DebounceException;
}
