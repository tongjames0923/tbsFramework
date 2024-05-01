package tbs.framework.sql.interfaces.impls.convertor;

import tbs.framework.base.intefaces.impls.chain.AbstractChain;

/**
 * 基础类型转换 CharSequence Number Boolean Character
 * @author Abstergo
 */
public class BaseTypeConvertor extends AbstractChain<Object, String> {
    @Override
    public void doChain(Object param) {
        if (param instanceof CharSequence ||
            param instanceof Number ||
            param instanceof Boolean ||
            param instanceof Character) {
            done(String.valueOf(param));
        }
    }
}
