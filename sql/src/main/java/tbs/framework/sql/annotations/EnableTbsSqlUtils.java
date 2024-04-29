package tbs.framework.sql.annotations;

import org.springframework.context.annotation.Import;
import tbs.framework.sql.config.SqlConfig;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(SqlConfig.class)
public @interface EnableTbsSqlUtils {
}
