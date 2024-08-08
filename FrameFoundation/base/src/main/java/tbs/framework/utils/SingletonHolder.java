package tbs.framework.utils;

import cn.hutool.extra.spring.SpringUtil;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * @author abstergo
 */
public class SingletonHolder {

    private static SingletonHolder instace;

    public SingletonHolder() {
        //防止直接实例化
        instace = this;
    }

    ConcurrentMap<Class<?>, Object> holderMap = new ConcurrentHashMap<>();

    public static <T> T getInstance(Class<? extends T> clazz, Function<Class<? extends T>, T> ifNull) {
        return (T)instace.holderMap.computeIfAbsent(clazz, k -> ifNull.apply(clazz));
    }

    public static <T> T getInstance(Class<? extends T> clazz) {
        return getInstance(clazz, SingletonHolder::fromSpring);
    }

    public static final <T> T fromSpring(Class<? extends T> c) {
        return SpringUtil.getBean(c);
    }
}
