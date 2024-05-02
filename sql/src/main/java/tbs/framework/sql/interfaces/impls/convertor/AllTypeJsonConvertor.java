package tbs.framework.sql.interfaces.impls.convertor;

import com.alibaba.fastjson2.JSON;
import tbs.framework.base.intefaces.impls.chain.AbstractChain;

/**
 * 支持所有类型的 转化为JSON字符串
 *
 * @author abstergo
 */
public class AllTypeJsonConvertor extends AbstractChain<Object, String> {
    @Override
    public void doChain(Object param) {
        if (null != param) {
            done(JSON.toJSONString(param));
        }
    }
}
