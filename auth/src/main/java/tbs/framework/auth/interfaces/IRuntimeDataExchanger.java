package tbs.framework.auth.interfaces;

import tbs.framework.auth.model.RuntimeData;

/**
 * 运行时数据转换，用于自动化装配运行时数据
 * @author abstergo
 */
public interface IRuntimeDataExchanger<T> {

    T exchange(RuntimeData data, T dest);
}
