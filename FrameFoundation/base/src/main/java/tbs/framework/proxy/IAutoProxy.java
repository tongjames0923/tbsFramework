package tbs.framework.proxy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface IAutoProxy {
    public void wiredValue(Field field, Object target);

    Object proxyExecute(Object proxy, Method method, Object[] args) throws Throwable;

    Class<?> requiredType();
}
