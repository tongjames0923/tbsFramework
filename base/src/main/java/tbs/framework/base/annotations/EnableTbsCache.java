package tbs.framework.base.annotations;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Import;
import tbs.framework.base.config.CacheConfig;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 启动缓存功能
 * @author Abstergo
 */
@EnableTbsFramework
@EnableCaching
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(CacheConfig.class)
public @interface EnableTbsCache {
}
