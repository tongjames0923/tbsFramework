package tbs.framework.base.interfaces;

import tbs.framework.base.annotations.NoInterceptMethod;

import java.util.List;

/**
 * @author abstergo
 */
public interface IInterceptedTarget {

    @NoInterceptMethod
    public List<IMethodInterceptHandler> handlers();
}
