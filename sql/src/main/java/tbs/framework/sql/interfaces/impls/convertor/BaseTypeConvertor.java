package tbs.framework.sql.interfaces.impls.convertor;

import tbs.framework.sql.interfaces.AbstractConvertor;

/**
 * 基础类型转换 CharSequence Number Boolean Character
 * @author Abstergo
 */
public class BaseTypeConvertor extends AbstractConvertor {

    @Override
    protected boolean support(Class<?> t) {
        return CharSequence.class.isAssignableFrom(t) ||
            Number.class.isAssignableFrom(t) ||
            Boolean.class.isAssignableFrom(t) ||
            CharSequence.class.isAssignableFrom(t);
    }

    @Override
    protected String doConvert(Object value) {
        return value.toString();
    }
}
