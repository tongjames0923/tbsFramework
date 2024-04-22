package tbs.framework.base.annotations;

import org.springframework.context.annotation.Import;
import tbs.framework.base.config.MultilingualConfig;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({MultilingualConfig.class})
public @interface EnableMultilingual {
}
