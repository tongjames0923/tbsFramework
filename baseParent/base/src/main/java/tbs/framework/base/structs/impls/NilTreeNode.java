package tbs.framework.base.structs.impls;

import tbs.framework.base.structs.ITree;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author abstergo
 */
public class NilTreeNode<T> implements ITree<T> {

    @Override
    public ITree getParent() {
        return null;
    }

    @Override
    public void setParent(ITree parent) {

    }

    @Override
    public void setNode(int index, ITree node) {

    }

    @Override
    public ITree getNode(int index) {
        return null;
    }

    @Override
    public void removeNode(int index) {

    }

    @Override
    public Collection<ITree<T>> allNode() {
        return List.of();
    }

    @Override
    public Set<Integer> allIndex() {
        return Set.of();
    }

    @Override
    public int index(ITree node) {
        return 0;
    }

    @Override
    public int maxIndex() {
        return 0;
    }

    @Override
    public int minIndex() {
        return 0;
    }

    @Override
    public void setValue(Object v) {

    }

    @Override
    public T getValue() {
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof NilTreeNode;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "NilTreeNode";
    }
}
