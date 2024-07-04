package tbs.framework.sql.annotations;

import org.springframework.stereotype.Component;
import tbs.framework.base.interfaces.IChainProvider;
import tbs.framework.sql.enums.QueryConnectorEnum;
import tbs.framework.sql.enums.QueryContrastEnum;
import tbs.framework.sql.interfaces.impls.provider.BuiltInValueConvertChainProvider;

import java.lang.annotation.*;

/**
 * The interface Query field.
 *
 * @author abstergo
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
@Repeatable(QueryFields.class)
@Component
public @interface QueryField {

    /**
     * 映射字段
     *
     * @return string
     */
    String map() default "";

    /**
     * 同一字段查询顺序，升序
     *
     * @return int
     */
    int index() default -1;

    /**
     * 关系运算
     *
     * @return query connector enum
     */
    QueryConnectorEnum connector() default QueryConnectorEnum.AND;

    /**
     * 比较运算
     *
     * @return query contrast enum
     */
    QueryContrastEnum contrast() default QueryContrastEnum.EQUAL;

    /**
     * 是否忽略大小写
     *
     * @return boolean
     */
    boolean ignoreCase() default false;

    /**
     * 是否忽略空值
     *
     * @return boolean
     */
    boolean ignoreNull() default true;

    /**
     * 责任链模式进行值转化，通过spring bean获取
     *
     * @return class
     */
    Class<? extends IChainProvider> valueMapper() default BuiltInValueConvertChainProvider.class;

}
