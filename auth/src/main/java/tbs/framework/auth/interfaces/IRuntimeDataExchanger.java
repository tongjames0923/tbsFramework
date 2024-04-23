package tbs.framework.auth.interfaces;

import tbs.framework.auth.model.RuntimeData;

/**
 * @author abstergo
 */
public interface IRuntimeDataExchanger<T> {

    T exchange(RuntimeData data, T dest);
}
