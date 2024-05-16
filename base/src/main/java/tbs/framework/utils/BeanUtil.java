package tbs.framework.utils;

import cn.hutool.extra.spring.SpringUtil;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import java.util.Map;
import java.util.regex.Pattern;

/**
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
     * @param bean
     */
    public static void autowireBean(Object bean) {
        AutowireCapableBeanFactory autowireCapableBeanFactory =
            SpringUtil.getApplicationContext().getAutowireCapableBeanFactory();
        autowireCapableBeanFactory.autowireBean(bean);
    }

    /**
     * 销毁bean
     *
     * @param beanClass  bean类型
     * @param beanPatten 匹配表达式
     */
    public static void destroyBean(String beanPatten, Class<?> beanClass) {
        Pattern pattern = Pattern.compile(beanPatten);
        for (Map.Entry<String, ?> entry : SpringUtil.getBeansOfType(beanClass).entrySet()) {
            if (pattern.matcher(entry.getKey()).matches()) {
                SpringUtil.getApplicationContext().getAutowireCapableBeanFactory().destroyBean(entry.getValue());
            }
        }
    }
}
