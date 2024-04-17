package tbs.framework.xxl.annotations;

import org.springframework.context.annotation.Import;
import tbs.framework.xxl.config.XxlJobConfig;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(XxlJobConfig.class)
public @interface EnableTbsXXL {
}
