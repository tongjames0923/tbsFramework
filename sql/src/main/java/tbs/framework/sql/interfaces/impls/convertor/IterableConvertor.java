package tbs.framework.sql.interfaces.impls.convertor;

import tbs.framework.sql.interfaces.AbstractConvertChainProvider;
import tbs.framework.sql.interfaces.AbstractConvertor;

/**
 * 遍历器值转换 Iterable
 * @author Abstergo
 */
public class IterableConvertor extends AbstractConvertor {
    @Override
    protected boolean support(Class<?> t) {
        return Iterable.class.isAssignableFrom(t);
    }

    private AbstractConvertChainProvider convertChainProvider;

    public IterableConvertor(AbstractConvertChainProvider convertChainProvider) {
        this.convertChainProvider = convertChainProvider;
    }

    @Override
    protected String doConvert(Object value) {
        final StringBuilder sb = new StringBuilder();
        for (final Object v : (Iterable<?>)value) {
            sb.append("'").append(AbstractConvertChainProvider.process(convertChainProvider, v)).append("',");
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }
}
