package tbs.framework.base.proxy;

import java.util.function.Function;

public interface IProxy {
    <R, P> R proxy(Function<P, R> function, P param);
}
