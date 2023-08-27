package main;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class BTree<T extends Comparable<T>> {
    private BTreeNode<T> root;
    private final int maxDegree;
    private final int maxKeys;

    public BTree(int maxDegree) {
        if (maxDegree < 3) {
            throw new IllegalArgumentException("Max degree must be 3 or greater.");
        }

        this.maxDegree = maxDegree;
        this.maxKeys = maxDegree - 1;
        root = new BTreeNode<>(true, true);
    }

    public BTreeNode<T> getRoot() {
        return this.root;
    }


    public int getMaxDegree() {
        return this.maxDegree;
    }

    public boolean search(T value) {
        return searchHelper(root, value);
    }

    private boolean searchHelper(BTreeNode<T> currentNode, T value) {
        int currentIdx = 0;

        for (var key : currentNode.keys) {
            if (value.compareTo(key) == 0) {
                return true;
            } else if (value.compareTo(key) > 0) {
                currentIdx += 1;
            }
        }

        if (currentNode.children.size() == 0) {
            return false;
        }

        return searchHelper(currentNode.children.get(currentIdx), value);
    }

    public void insert(T item) {
        insertHelper(this.root, new Stack<>(), item);
    }

    private void insertHelper(@NotNull BTreeNode<T> currentNode, Stack<BTreeNode<T>> prevNodes, T item) {
        // This is the basic case where we find a leaf node that is non-empty,
        // and we insert directly into it.
        if (currentNode.keys.size() < maxKeys && currentNode.isLeaf) {
            currentNode.addKey(item);
        }
        // This is the case where we have to split the current node under consideration
        // and possibly funnel nodes upwards
        else if (currentNode.keys.size() == maxKeys && currentNode.isLeaf) {
            splitInsertHelper(currentNode, prevNodes, item);
        }
        // This is the case where we need to continue our search to find the
        // leaf node where this item belongs
        else {
            // Find the proper place to continue search within the children of
            // this node.
            int currentIdx = 0;

            for (var key : currentNode.keys) {
                if (item.compareTo(key) > 0) {
                    currentIdx++;
                } else {
                    break;
                }
            }

            prevNodes.push(currentNode);
            insertHelper(currentNode.children.get(currentIdx), prevNodes, item);
        }
    }

    private void splitInsertHelper(@NotNull BTreeNode<T> currentNode, Stack<BTreeNode<T>> prevNodes, T item) {
        // Begin by inserting the new item into the overfilled node.
        currentNode.addKey(item);
        split(currentNode, prevNodes);
    }

    private void split(@NotNull BTreeNode<T> currentNode, Stack<BTreeNode<T>> prevNodes) {
        // Then find the median node, excise it, and then get the two new nodes.
        int medianIdx = Math.floorDiv(currentNode.keys.size(), 2);
        var medianItem = currentNode.keys.get(medianIdx);

        // Gets the left keys and the right keys while removing the median key
        // from both.
        var leftKeyList = new ArrayList<>(currentNode.keys.subList(0, medianIdx));
        var rightKeyList = new ArrayList<>(currentNode.keys.subList(medianIdx + 1, currentNode.keys.size()));

        // If the current node is the root we need to deal with making the
        // median key the root node of the tree.
        if (currentNode.isRoot) {
            BTreeNode<T> newRootNode;
            List<T> rootKeyList = new ArrayList<>();
            rootKeyList.add(medianItem);

            // If the current node has children then we have to take account of
            // them as far as left and right splitting
            if (currentNode.children.size() > 0) {
                int childrenMedianIdx = Math.floorDiv(currentNode.keys.size(), 2);

                var leftChildren = new ArrayList<>(currentNode.children.subList(0, childrenMedianIdx + 1));
                var rightChildren = new ArrayList<>(currentNode.children.subList(childrenMedianIdx + 1, currentNode.children.size()));

                BTreeNode<T> leftNode = new BTreeNode<>(currentNode.isLeaf, false, leftKeyList, leftChildren);
                BTreeNode<T> rightNode = new BTreeNode<>(currentNode.isLeaf, false, rightKeyList, rightChildren);

                List<BTreeNode<T>> rootChildrenList = new ArrayList<>();
                rootChildrenList.add(leftNode);
                rootChildrenList.add(rightNode);

                newRootNode = new BTreeNode<>(false, true, rootKeyList, rootChildrenList);
            }
            // The current node has no children, and we simply just add a new array
            // list for any future children.
            else {
                List<BTreeNode<T>> rootChildrenList = new ArrayList<>();
                BTreeNode<T> leftNode = new BTreeNode<>(currentNode.isLeaf, false, leftKeyList, new ArrayList<>());
                BTreeNode<T> rightNode = new BTreeNode<>(currentNode.isLeaf, false, rightKeyList, new ArrayList<>());

                rootChildrenList.add(leftNode);
                rootChildrenList.add(rightNode);

                newRootNode = new BTreeNode<>(false, true, rootKeyList, rootChildrenList);
            }

            this.root = newRootNode;
            return;
        }

        // If the current node is an internal node then we still simply split,
        // but we have to place the children into the new left and right nodes
        // correctly.
        if (currentNode.children.size() > 0) {
            int childrenMedianIdx = Math.floorDiv(currentNode.keys.size(), 2);
            var leftChildren = new ArrayList<>(currentNode.children.subList(0, childrenMedianIdx + 1));
            var rightChildren = new ArrayList<>(currentNode.children.subList(childrenMedianIdx + 1, currentNode.children.size()));

            var leftNode = new BTreeNode<>(currentNode.isLeaf, false, leftKeyList, leftChildren);
            var rightNode = new BTreeNode<>(currentNode.isLeaf, false, rightKeyList, rightChildren);

            var parentNode = prevNodes.pop();

            int insertMedianIdx = 0;
            for (var key : parentNode.keys) {
                if (medianItem.compareTo(key) < 0) {
                    break;
                }
                insertMedianIdx++;
            }

            parentNode.keys.add(insertMedianIdx, medianItem);

            parentNode.children.remove(currentNode);
            parentNode.children.add(insertMedianIdx, leftNode);
            parentNode.children.add(insertMedianIdx + 1, rightNode);

            // We could have possibly overfilled the parent node so now check
            // to see if it needs to be split
            if (parentNode.keys.size() > maxKeys) {
                split(parentNode, prevNodes);
            }
        }

        // Case where the current node has no children, and we simply split the
        // nodes and make them children of the parent node.
        if (currentNode.children.size() == 0) {
            var parentNode = prevNodes.pop();

            // Find position to insert the median key and this will also give us
            // where to insert the children at.
            int insertMedianIdx = 0;
            for (var key : parentNode.keys) {
                if (medianItem.compareTo(key) < 0) {
                    break;
                }
                insertMedianIdx++;
            }

            parentNode.keys.add(insertMedianIdx, medianItem);
            parentNode.children.remove(currentNode);

            var leftNode = new BTreeNode<>(true, false, leftKeyList, new ArrayList<>());
            var rightNode = new BTreeNode<>(true, false, rightKeyList, new ArrayList<>());

            parentNode.children.add(insertMedianIdx, leftNode);
            parentNode.children.add(insertMedianIdx + 1, rightNode);

            // We could have possibly overfilled the parent node so now check
            // to see if it needs to be split
            if (parentNode.keys.size() > maxKeys) {
                split(parentNode, prevNodes);
            }
        }
    }
}
