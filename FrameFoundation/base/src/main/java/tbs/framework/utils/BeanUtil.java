package tbs.framework.utils;

import cn.hutool.extra.spring.SpringUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import tbs.framework.base.interfaces.FunctionWithThrows;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * The type Bean util.
 *
 * @author abstergo
 */
public class BeanUtil {
    /**
     * 注册bean 包括自动装配
     *
     * @param bean     对象
     * @param beanName bean 名
     */
    public static void registerBean(Object bean, String beanName) {
        autowireBean(bean);
        SpringUtil.registerBean(beanName, bean);
    }

    /**
     * 装配bean
     *
     * @param bean the bean
     */
    public static void autowireBean(Object bean) {
        AutowireCapableBeanFactory autowireCapableBeanFactory =
            SpringUtil.getApplicationContext().getAutowireCapableBeanFactory();
        autowireCapableBeanFactory.autowireBean(bean);
    }

    /**
     * 销毁bean
     *
     * @param beanPatten 匹配表达式
     * @param beanClass  bean类型
     */
    public static void destroyBean(String beanPatten, Class<?> beanClass) {
        Pattern pattern = Pattern.compile(beanPatten);
        for (Map.Entry<String, ?> entry : SpringUtil.getBeansOfType(beanClass).entrySet()) {
            if (pattern.matcher(entry.getKey()).matches()) {
                SpringUtil.getApplicationContext().getAutowireCapableBeanFactory().destroyBean(entry.getValue());
            }
        }
    }

    /**
     * Build bean from properties t.
     *
     * @param <T>         the type parameter
     * @param defaultBean the default bean
     * @param property    the property
     * @param func        the func
     * @return the t
     * @throws Exception the exception
     */
    public static <T> T buildBeanFromProperties(T defaultBean, Class<? extends T> property,
        FunctionWithThrows<Class<? extends T>, T, Exception> func) throws Exception {
        if (property == null) {
            return defaultBean;
        }
        if (func == null) {
            func = BeanUtil::useEmptyArgs;
        }
        return func.apply(property);
    }

    /**
     * Use empty args t.
     *
     * @param <T> the type parameter
     * @param p   the p
     * @return the t
     * @throws Exception the exception
     */
    public static <T> T useEmptyArgs(Class<? extends T> p) throws Exception {
        return p.getConstructor().newInstance();
    }

    /**
     * Gets as.
     *
     * @param <T>  the type parameter
     * @param <T1> the type parameter
     * @param v    the v
     * @return the as
     */
    public static <T, T1 extends T> T1 getAs(T v) {
        return (T1)v;
    }

    public static final boolean iSBaseFrom(@NotNull Class<?> target, @NotNull Class<?> base) {
        return base.isAssignableFrom(target);
    }
}
