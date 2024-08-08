package tbs.framework.proxy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 自动代理接口，定义了代理对象需要实现的方法 该接口用于在不修改目标对象代码的情况下，通过反射机制为对象提供额外的功能实现，如日志、事务等
 * @author abstergo
 */
public interface IAutoProxy {
    /**
     * 根据字段和bean对象配置相关参数
     *
     * @param field  注入的字段
     * @param target 目标对象，即需要进行值注入的对象
     */
    public void wiredValue(Field field, Object target);

    /**
     * 执行代理对象的方法拦截
     *
     * @param proxy  代理对象本身
     * @param method 被调用的方法
     * @param args   方法的参数
     * @return 方法的执行结果
     * @throws Throwable 方法执行过程中可能抛出的异常
     */
    Object proxyExecute(Object proxy, Method method, Object[] args) throws Throwable;

    /**
     * 获取代理对象所需的类型
     *
     * @return 代理对象的类型
     */
    Class<?> requiredType();
}

