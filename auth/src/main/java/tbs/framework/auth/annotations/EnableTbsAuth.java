package tbs.framework.auth.annotations;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import tbs.framework.auth.config.AuthConfig;
import tbs.framework.auth.properties.AuthProperty;
import tbs.framework.base.annotations.EnableTbsFramework;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author abstergo
 */
@EnableTbsFramework
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(AuthConfig.class)
@EnableConfigurationProperties(AuthProperty.class)
public @interface EnableTbsAuth {
}
