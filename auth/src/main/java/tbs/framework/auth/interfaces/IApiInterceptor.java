package tbs.framework.auth.interfaces;

import java.lang.reflect.Method;

/**
 * 定义一个API拦截器接口，用于在API调用前后进行拦截处理。
 * @author abstergo
 */
public interface IApiInterceptor {
    /**
     * 在API方法调用之前进行拦截处理。
     *
     * @param function API方法对象
     * @param target   API方法所在的对象
     * @param args     API方法参数
     * @throws RuntimeException 异常信息
     */
    void beforeInvoke(Method function, Object target, Object[] args) throws RuntimeException;

    /**
     * 在API方法调用之后进行拦截处理。
     *
     * @param function API方法对象
     * @param target   API方法所在的对象
     * @param args     API方法参数
     * @param result   API方法返回值
     * @throws RuntimeException 异常信息
     */
    void afterInvoke(Method function, Object target, Object[] args, Object result) throws RuntimeException;

    /**
     * 判断当前拦截器是否支持指定的URL。
     *
     * @param url URL地址
     * @return 如果支持该URL，则返回true，否则返回false
     */
    boolean support(String url);
}
