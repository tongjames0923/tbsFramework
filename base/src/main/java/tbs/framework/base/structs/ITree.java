package tbs.framework.base.structs;

import tbs.framework.base.structs.impls.NilTreeNode;

import java.util.Collection;
import java.util.Set;

/**
 * @author abstergo
 */
public interface ITree<T> {

    public static final int NIL = Integer.MIN_VALUE;

    ITree getParent();

    void setParent(ITree parent);

    void setNode(int index, ITree node);

    ITree getNode(int index);

    void removeNode(int index);

    Collection<ITree<T>> allNode();

    Set<Integer> allIndex();

    int index(ITree node);

    int maxIndex();

    int minIndex();

    default boolean contains(ITree node) {
        return index(node) != NIL;
    }

    void setValue(T v);

    T getValue();

    static boolean isNil(ITree n) {
        return n == null || n instanceof NilTreeNode;
    }
}
