package tbs.framework.sql.interfaces.impls.provider;

import tbs.framework.sql.interfaces.AbstractConvertChainProvider;
import tbs.framework.sql.interfaces.AbstractConvertor;
import tbs.framework.sql.interfaces.impls.convertor.BaseTypeConvertor;
import tbs.framework.sql.interfaces.impls.convertor.DateValueConvertor;
import tbs.framework.sql.interfaces.impls.convertor.IterableConvertor;
import tbs.framework.sql.interfaces.impls.convertor.NullValueConvertor;

import java.util.Arrays;
import java.util.List;

/**
 * 内置的值转换责任链提供者， 依次包含空值转换、IOrderedModel类型、基础类型（数值，字符串，bool,字符）转换 ，Iterable转换，日期值（Date和Temporal）转换，
 * @author abstergo
 */
public class BuiltInValueConvertChainProvider extends AbstractConvertChainProvider {
    @Override
    protected List<AbstractConvertor> getConvertors() {
        return Arrays.asList(new NullValueConvertor(), new IterableConvertor(this), new BaseTypeConvertor(),
            new DateValueConvertor());
    }
}
