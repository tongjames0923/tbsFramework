package tbs.framework.sql.annotations;

import java.lang.annotation.*;

/**
 * 设置查询where语句字段的顺序，升序
 *
 * @author abstergo
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface QueryIndex {

    /**
     * Index int.
     *
     * @return the int
     */
    int index() default 1000;
}
