package tbs.framework.base.annotations;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import tbs.framework.base.config.AsyncConfig;
import tbs.framework.base.properties.ExecutorProperty;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableConfigurationProperties({ExecutorProperty.class})
@Import(AsyncConfig.class)
public @interface EnableTbsAsync {
}
