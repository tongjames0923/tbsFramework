package tbs.framework.sql.interfaces.impls.convertor;

import tbs.framework.base.interfaces.impls.chain.AbstractChain;

/**
 * 空值转换 转换成null
 *
 * @author Abstergo
 */
public class NullValueConvertor extends AbstractChain<Object, String> {
    @Override
    public void doChain(Object param) {
        if (null == param) {
            done("null");
        }
    }
}
