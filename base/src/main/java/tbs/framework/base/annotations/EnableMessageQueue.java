package tbs.framework.base.annotations;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import tbs.framework.base.config.MultilingualConfig;
import tbs.framework.base.properties.LocalProperty;
import tbs.framework.base.properties.MqProperty;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@EnableTbsFramework
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(MultilingualConfig.class)
@EnableConfigurationProperties(MqProperty.class)
public @interface EnableMessageQueue {
}
