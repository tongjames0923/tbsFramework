package tbs.framework.utils;

import cn.hutool.extra.spring.SpringUtil;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

/**
 * 单例持有器，用于缓存单例对象。
 *
 * @author abstergo
 */
public class SingletonHolder {
    /**
     * 使用volatile关键字保证可见性和有序性
     */
    private static volatile SingletonHolder instance;

    private static final AtomicBoolean HAS_BEEN_INIT = new AtomicBoolean(false);

    private final ConcurrentMap<Class<?>, Object> holderMap = new ConcurrentHashMap<>();

    public SingletonHolder() {
        if (HAS_BEEN_INIT.compareAndSet(false, true)) {
            instance = this;
        } else {
            throw new IllegalStateException("SingletonHolder cannot be instantiated more than once.");
        }
    }

    /**
     * 获取指定类型的单例实例。
     *
     * @param clazz  类型
     * @param ifNull 无实例时的回调
     * @param <T>    泛型类型
     * @return 单例实例
     */
    public static <T> T getInstance(Class<? extends T> clazz, Function<Class<? extends T>, T> ifNull) {
        if (instance == null) {
            synchronized (SingletonHolder.class) {
                if (instance == null) {
                    instance = new SingletonHolder();
                }
            }
        }
        return (T)instance.holderMap.computeIfAbsent(clazz, k -> ifNull.apply(clazz));
    }

    /**
     * 获取指定类型的单例实例。
     *
     * @param clazz 类型
     * @param <T>   泛型类型
     * @return 单例实例
     */
    public static <T> T getInstance(Class<? extends T> clazz) {
        return getInstance(clazz, SingletonHolder::fromSpring);
    }

    /**
     * 从Spring容器获取Bean。
     *
     * 注意：此方法假设Spring容器中的Bean为单例模式。
     *
     * @param clazz 类型
     * @param <T>   泛型类型
     * @return Bean实例
     */
    public static <T> T fromSpring(Class<? extends T> clazz) {
        return SpringUtil.getBean(clazz);
    }
}
