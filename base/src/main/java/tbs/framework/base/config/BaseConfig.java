package tbs.framework.base.config;


import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.EnableSpringUtil;
import cn.hutool.extra.spring.SpringUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import tbs.framework.base.constants.BeanNameConstant;
import tbs.framework.base.log.ILogProvider;
import tbs.framework.base.log.impls.Slf4jLoggerProvider;
import tbs.framework.base.properties.BaseProperty;
import tbs.framework.base.utils.LogUtil;

import javax.annotation.Resource;

@EnableSpringUtil
@Order(0)
public class BaseConfig {

    @Resource
    BaseProperty baseProperty;

    @Bean(name = BeanNameConstant.BUILTIN_LOGGER)
    @Order(0)
    public ILogProvider getLogger() {
        if (StrUtil.isEmpty(baseProperty.getLoggerProvider())) {
            return new Slf4jLoggerProvider();
        }
        return SpringUtil.getBean(baseProperty.getLoggerProvider());
    }

    @Bean
    @Order(10)
    public LogUtil getLogUtil() {
        return new LogUtil();
    }
}
