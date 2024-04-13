package tbs.framework.base.annotations;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import tbs.framework.base.config.BaseConfig;
import tbs.framework.base.properties.BaseProperty;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableConfigurationProperties({BaseProperty.class})
@Import(BaseConfig.class)

public @interface EnableTbsFramework {
}
