package tbs.framework.auth.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 自动装配运行时数据
 *
 * @author abstergo
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApplyRuntimeData {
}
