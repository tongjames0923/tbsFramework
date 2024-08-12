package tbs.framework.sql.interfaces;

import tbs.framework.base.interfaces.IChainProvider;
import tbs.framework.base.interfaces.impls.chain.AbstractChain;
import tbs.framework.utils.ChainUtil;

import java.util.List;

/**
 * 抽象转换链提供器
 * @author abstergo
 */
public abstract class AbstractConvertChainProvider implements IChainProvider<Object, String> {

    /**
     * 所有的转换器
     *
     * @return
     */
    protected abstract List<AbstractConvertor> getConvertors();

    @Override
    public AbstractChain<Object, String> beginChain() {

        AbstractChain.Builder builder = new AbstractChain.Builder();

        for (AbstractConvertor convertor : getConvertors()) {
            builder.add(convertor);
        }
        return builder.build();
    }

    public static final String process(AbstractConvertChainProvider chainProvider, Object val) {
        return ChainUtil.process(chainProvider.beginChain(), val).toString();
    }
}
