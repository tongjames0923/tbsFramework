package tbs.framework.sql.interfaces.impls.convertor;

import tbs.framework.sql.interfaces.AbstractConvertor;

/**
 * 空值转换 转换成null
 *
 * @author Abstergo
 */
public class NullValueConvertor extends AbstractConvertor {
    @Override
    protected boolean support(Class<?> t) {
        return t == null;
    }

    @Override
    protected String doConvert(Object value) {
        return "null";
    }
}
