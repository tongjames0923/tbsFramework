package tbs.framework.base.structs.impls;

import tbs.framework.base.structs.ITree;

import java.util.*;

/**
 * @author abstergo
 */
public class SimpleMultibranchTree<T> implements ITree<T> {
    private ITree<T> parent = new NilTreeNode<>();
    private T v;

    private Map<Integer, ITree<T>> children = new HashMap<>();

    private int maxIndex = 0;

    private int minIndex = 0;

    @Override
    public ITree getParent() {
        return parent;
    }

    @Override
    public void setParent(ITree parent) {
        this.parent = parent;
    }

    @Override
    public void setNode(int index, ITree node) {
        if (ITree.isNil(node)) {
            return;
        }
        if (index == NIL) {
            throw new RuntimeException("can not set index as nil index");
        }
        if (!ITree.isNil(node.getParent())) {
            throw new RuntimeException("child node should not have parent");
        }
        node.setParent(this);
        children.put(index, node);
        if (maxIndex < index) {
            maxIndex = index;
        }
        if (minIndex > index) {
            minIndex = index;
        }
    }

    @Override
    public ITree getNode(int index) {
        return children.get(index);
    }

    @Override
    public void removeNode(int index) {
        children.remove(index);
    }

    @Override
    public Collection<ITree<T>> allNode() {
        return children.values();
    }

    @Override
    public Set<Integer> allIndex() {
        return children.keySet();
    }

    @Override
    public int index(ITree node) {
        if (ITree.isNil(node)) {
            return NIL;
        }
        Set<Map.Entry<Integer, ITree<T>>> entrys = children.entrySet();
        for (Map.Entry<Integer, ITree<T>> entry : entrys) {
            if (ITree.isNil(entry.getValue())) {
                children.remove(entry.getKey());
            }
            if (Objects.equals(entry.getValue(), node)) {
                return entry.getKey();
            }
        }
        return NIL;
    }

    @Override
    public int maxIndex() {
        return maxIndex;
    }

    @Override
    public int minIndex() {
        return minIndex;
    }

    @Override
    public void setValue(T v) {
        this.v = v;
    }

    @Override
    public T getValue() {
        return v;
    }
}
