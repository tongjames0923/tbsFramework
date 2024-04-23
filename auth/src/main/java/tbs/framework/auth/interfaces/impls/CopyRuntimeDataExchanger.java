package tbs.framework.auth.interfaces.impls;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import tbs.framework.auth.interfaces.IRuntimeDataExchanger;
import tbs.framework.auth.model.RuntimeData;

/**
 * @author abstergo
 */
public class CopyRuntimeDataExchanger<T> implements IRuntimeDataExchanger<T> {

    @Override
    public T exchange(RuntimeData data, T val) {
        BeanUtil.copyProperties(data, val, CopyOptions.create().ignoreNullValue().ignoreError().ignoreCase());
        return val;
    }
}
