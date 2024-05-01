package tbs.framework.sql.interfaces.impls.convertor;

import tbs.framework.base.intefaces.impls.chain.AbstractChain;

/**
 * 空值转换 转换成null
 *
 * @author Abstergo
 */
public class NullValueConvertor<P> extends AbstractChain<P, String> {
    @Override
    public void doChain(Object param) {
        if (param == null) {
            done("null");
        }
    }
}
