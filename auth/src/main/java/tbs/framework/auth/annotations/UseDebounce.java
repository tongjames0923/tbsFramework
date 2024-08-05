package tbs.framework.auth.annotations;

import org.springframework.context.annotation.Import;
import tbs.framework.auth.config.DebounceConfig;
import tbs.framework.base.annotations.EnableTbsFramework;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@EnableTbsFramework
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(DebounceConfig.class)
public @interface UseDebounce {
}
