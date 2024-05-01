package tbs.framework.sql.interfaces.impls.provider;

import tbs.framework.base.intefaces.IChainProvider;
import tbs.framework.base.intefaces.impls.chain.AbstractChain;
import tbs.framework.sql.interfaces.impls.convertor.BaseTypeConvertor;
import tbs.framework.sql.interfaces.impls.convertor.DateValueConvertor;
import tbs.framework.sql.interfaces.impls.convertor.IterableConvertor;
import tbs.framework.sql.interfaces.impls.convertor.NullValueConvertor;

/**
 * 内置的值转换责任链提供者， 依次包含空值转换、基础类型（数值，字符串，bool,字符）转换 ，Iterable转换，日期值（Date和Temporal）转换，
 * @author abstergo
 */
public class BuiltInValueConvertChainProvider implements IChainProvider<Object, String> {
    @Override
    public AbstractChain<Object, String> beginChain() {
        return new AbstractChain.Builder<Object, String, AbstractChain<Object, String>>().add(new NullValueConvertor())
            .add(new BaseTypeConvertor())
            .add(new IterableConvertor()).add(new DateValueConvertor()).build();
    }
}
