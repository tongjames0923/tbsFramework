package tbs.framework.utils;

import cn.hutool.extra.spring.SpringUtil;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

/**
 * @author abstergo
 */
public class BeanUtil {
    public static void registerBean(Object bean, String beanName) {
        AutowireCapableBeanFactory autowireCapableBeanFactory =
            SpringUtil.getApplicationContext().getAutowireCapableBeanFactory();
        autowireCapableBeanFactory.autowireBean(bean);
        SpringUtil.registerBean(beanName, bean);
    }
}
