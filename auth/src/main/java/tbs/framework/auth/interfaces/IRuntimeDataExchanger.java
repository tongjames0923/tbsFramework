package tbs.framework.auth.interfaces;

import tbs.framework.auth.model.RuntimeData;

/**
 * 运行时数据转换，用于自动化装配运行时数据
 *
 * @author abstergo
 */
public interface IRuntimeDataExchanger<T> {

    /**
     * 转换数据，将运行时数据复制到目标类中
     *
     * @param data 运行时数据
     * @param dest 目标数据
     * @return
     */
    T exchange(RuntimeData data, T dest);
}
