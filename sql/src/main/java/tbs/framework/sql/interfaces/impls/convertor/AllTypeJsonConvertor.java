package tbs.framework.sql.interfaces.impls.convertor;

import com.alibaba.fastjson2.JSON;
import tbs.framework.sql.interfaces.AbstractConvertor;

/**
 * 支持所有类型的 转化为JSON字符串
 *
 * @author abstergo
 */
public class AllTypeJsonConvertor extends AbstractConvertor {
    @Override
    protected boolean support(Class<?> t) {
        return true;
    }

    @Override
    protected String doConvert(Object value) {
        return JSON.toJSONString(value);
    }

}
