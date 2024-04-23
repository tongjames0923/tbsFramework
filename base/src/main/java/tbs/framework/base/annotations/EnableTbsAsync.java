package tbs.framework.base.annotations;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import tbs.framework.base.config.AsyncConfig;
import tbs.framework.base.config.BaseConfig;
import tbs.framework.base.properties.ExecutorProperty;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Abstergo
 */
@EnableAsync
@EnableScheduling
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableConfigurationProperties({ExecutorProperty.class})
@Import(AsyncConfig.class)
public @interface EnableTbsAsync {
}
