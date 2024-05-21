package tbs.framework.base.structs.impls;

import tbs.framework.base.structs.ITree;

import java.util.function.Function;

/**
 * @author abstergo
 */
public class TreeUtil {

    public static interface ITreeNodeForeach<T> {
        void accept(ITree<T> patent, T v, int level);
    }

    public static <T> ITree<T> appendNode(ITree<T> tree, T value, Function<T, ITree<T>> valueMapper) {
        if (ITree.isNil(tree)) {
            throw new RuntimeException("Tree is empty");
        }
        ITree<T> node = valueMapper.apply(value);
        tree.setNode(tree.maxIndex() + 1, node);
        return node;
    }

    private static <T> void foreach_m(ITree<T> t, ITreeNodeForeach<T> pre, ITreeNodeForeach<T> after, int level) {
        if (ITree.isNil(t)) {
            return;
        }
        if (pre != null) {
            pre.accept(t.getParent(), t.getValue(), level);
        }
        for (ITree<T> c : t.allNode()) {
            foreach_m(c, pre, after, level + 1);
        }
        if (after != null) {
            after.accept(t.getParent(), t.getValue(), level);
        }
    }

    public static <T> void foreach(ITree<T> t, ITreeNodeForeach<T> pre, ITreeNodeForeach<T> after) {
        foreach_m(t, pre, after, 0);
    }

    public static <T> SimpleMultibranchTree<T> multiBranchTree(T v) {
        SimpleMultibranchTree<T> tree = new SimpleMultibranchTree<>();
        tree.setValue(v);
        return tree;
    }

}
