package tbs.framework.base.annotations;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import tbs.framework.base.config.MqConfig;
import tbs.framework.base.properties.MqProperty;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@EnableTbsFramework
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(MqConfig.class)
@EnableConfigurationProperties(MqProperty.class)
public @interface EnableMessageQueue {
}
