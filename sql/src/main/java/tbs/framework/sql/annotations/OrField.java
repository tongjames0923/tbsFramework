package tbs.framework.sql.annotations;

import java.lang.annotation.*;

/**
 * 字段间或连接符
 *
 * @author abstergo
 */
@Target(ElementType.FIELD)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface OrField {
}
