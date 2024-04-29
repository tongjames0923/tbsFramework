package tbs.framework.auth.interfaces.impls;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import tbs.framework.auth.interfaces.IRuntimeDataExchanger;
import tbs.framework.auth.model.RuntimeData;

/**
 * 简单复制运行时数据
 * @author abstergo
 */
public class CopyRuntimeDataExchanger<T> implements IRuntimeDataExchanger<T> {

    @Override
    public T exchange(final RuntimeData data, final T val) {
        BeanUtil.copyProperties(data, val, CopyOptions.create().ignoreNullValue().ignoreError().ignoreCase());
        return val;
    }
}
