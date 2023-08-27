package main;

import org.javatuples.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class BTreeNode<T extends Comparable<T>> {
    List<T> keys;
    List<BTreeNode<T>> children;
    boolean isLeaf;
    boolean isRoot;

    BTreeNode(boolean isLeaf, boolean isRoot) {
        this.keys = new ArrayList<>();
        this.children = new ArrayList<>();
        this.isLeaf = isLeaf;
        this.isRoot = isRoot;
    }

    BTreeNode(boolean isLeaf, boolean isRoot, List<T> keys, List<BTreeNode<T>> children) {
        this.keys = keys;
        this.children = children;
        this.isLeaf = isLeaf;
        this.isRoot = isRoot;
    }

    void addKey(T key) {
        keys.add(key);
        Collections.sort(keys);
    }

    @Override
    public String toString() {
        return recursiveStringWriter();
    }

    public String recursiveStringWriter() {
        return recursiveStringWriterHelper(this);
    }

    private @NotNull String recursiveStringWriterHelper(BTreeNode<T> node) {
        StringBuilder sb = new StringBuilder();
        Stack<Pair<BTreeNode<T>, Integer>> nodeQueue = new Stack<>();

        nodeQueue.add(new Pair<>(node, 0));

        while (!nodeQueue.isEmpty()) {
            var currentNode = nodeQueue.pop();
            sb.append(charRepeat(currentNode.getValue1()));
            sb.append("Keys: [ ");

            for (var key : currentNode.getValue0().keys) {
                sb.append(key).append(" ");
            }

            sb.append("]\n");

            var currentNodeChildren = currentNode.getValue0().children;
            ListIterator<BTreeNode<T>> childIterator = currentNodeChildren.listIterator(currentNodeChildren.size());

            while (childIterator.hasPrevious()) {
                nodeQueue.add(new Pair<>(childIterator.previous(), currentNode.getValue1() + 1));
            }
        }

        return sb.toString();
    }

    private @NotNull String charRepeat(int n) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0 ; i < n; i++) {
            sb.append(' ').append(' ');
        }

        return sb.toString();
    }
}