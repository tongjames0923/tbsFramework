package tbs.framework.base.intefaces;

/**
 * 带异常的function
 * @author Abstergo
 * @param <P>
 * @param <R>
 * @param <E>
 */
public interface FunctionWithThrows<P, R, E extends Throwable> {
    R apply(P p) throws E;
}
