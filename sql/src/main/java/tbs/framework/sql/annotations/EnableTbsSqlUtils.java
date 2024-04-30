package tbs.framework.sql.annotations;

import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import tbs.framework.base.annotations.EnableTbsFramework;
import tbs.framework.sql.config.SqlConfig;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@EnableTbsFramework
@EnableTransactionManagement
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(SqlConfig.class)
public @interface EnableTbsSqlUtils {
}
