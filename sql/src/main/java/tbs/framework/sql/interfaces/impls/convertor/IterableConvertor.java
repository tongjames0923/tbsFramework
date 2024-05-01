package tbs.framework.sql.interfaces.impls.convertor;

import tbs.framework.base.intefaces.impls.chain.AbstractChain;

/**
 * 遍历器值转换 Iterable
 * @author Abstergo
 */
public class IterableConvertor<P> extends AbstractChain<P, String> {
    @Override
    public void doChain(Object value) {
        if (value instanceof Iterable) {
            final StringBuilder sb = new StringBuilder();
            for (final Object v : (Iterable<?>)value) {
                sb.append("'").append(v.toString()).append("',");
            }
            sb.setLength(sb.length() - 1);
            done(sb.toString());
        }
    }
}
