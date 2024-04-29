package tbs.framework.sql.annotations;

import org.springframework.stereotype.Component;
import tbs.framework.sql.enums.QueryConnectorEnum;
import tbs.framework.sql.enums.QueryContrastEnum;
import tbs.framework.sql.interfaces.IValueMapper;
import tbs.framework.sql.interfaces.impls.SimpleValueMapper;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
@Repeatable(QueryFields.class)
@Component
public @interface QueryField {

    /**
     * 映射字段
     *
     * @return
     */
    String map() default "";

    /**
     * 运行顺序
     *
     * @return
     */

    int index() default -1;

    /**
     * 关系运算
     *
     * @return
     */
    QueryConnectorEnum connector() default QueryConnectorEnum.AND;

    /**
     * 比较运算
     *
     * @return
     */
    QueryContrastEnum contrast() default QueryContrastEnum.EQUAL;

    /**
     * 是否忽略大小写
     *
     * @return
     */

    boolean ignoreCase() default false;

    /**
     * 是否忽略空值
     *
     * @return
     */
    boolean ignoreNull() default true;

    Class<? extends IValueMapper> valueMapper() default SimpleValueMapper.class;

}
