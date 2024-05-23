package tbs.framework.sql.annotations;

import tbs.framework.sql.enums.QueryOrderEnum;

import java.lang.annotation.*;

/**
 * The interface Query order field.
 * @author abstergo
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface QueryOrderField {

    /**
     * Order query order enum.
     *
     * @return the query order enum
     */
    QueryOrderEnum order() default QueryOrderEnum.ASC;
}
