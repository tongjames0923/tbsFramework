package tbs.framework.base.annotations;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import tbs.framework.base.config.AsyncConfig;
import tbs.framework.base.properties.AsyncTaskProperty;
import tbs.framework.base.properties.ExecutorProperty;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 启动异步功能
 */
@EnableTbsFramework
@EnableAsync
@EnableScheduling
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableConfigurationProperties({ExecutorProperty.class, AsyncTaskProperty.class})
@Import(AsyncConfig.class)
public @interface EnableTbsAsync {
}
