package tbs.framework.base.intefaces;

public interface FunctionWithThrows<P, R, E extends Throwable> {
    R apply(P p) throws E;
}
