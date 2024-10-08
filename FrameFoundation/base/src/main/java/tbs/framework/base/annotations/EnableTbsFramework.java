package tbs.framework.base.annotations;

import cn.hutool.extra.spring.EnableSpringUtil;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import tbs.framework.base.config.BaseConfig;
import tbs.framework.base.properties.BaseProperty;
import tbs.framework.base.properties.LockProperty;
import tbs.framework.base.properties.MqProperty;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 启动常用框架功能
 */
@EnableSpringUtil
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableConfigurationProperties({BaseProperty.class, LockProperty.class, MqProperty.class})
@Import(BaseConfig.class)
@EnableAspectJAutoProxy(proxyTargetClass = true)
public @interface EnableTbsFramework {
}
