package tbs.framework.base.proxy;

import java.util.Optional;
import java.util.function.Function;

public interface IProxy {
    <R, P> Optional<R> proxy(Function<P, R> function, P param);
}
