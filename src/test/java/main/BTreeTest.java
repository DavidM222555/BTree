package main;

import com.google.common.collect.Comparators;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

class BTreeTest {
    boolean validateSearchProperty(@NotNull BTree<Integer> btree) {
        // Check that all nodes adhere to the search property for B-trees, that is,
        // that all children of a node are less than or greater than the corresponding
        // keys above them depending on orientation.
        Queue<BTreeNode<Integer>> queueOfNodes = new LinkedList<>();
        queueOfNodes.add(btree.root);

        // Test a given node, add its children to the queue, and so on until
        // the queue is exhausted
        while (!queueOfNodes.isEmpty()) {
            var currentNode = queueOfNodes.poll();

            if (!validateSearchPropertyForNode(currentNode)) {
                return false;
            }

            queueOfNodes.addAll(currentNode.children);
        }

        return true;
    }

    boolean validateSearchPropertyForNode(@NotNull BTreeNode<Integer> node) {
        var nodeChildren = node.children;
        var nodeKeys = node.keys;

        // Begin by ensuring that the node itself is sorted
        if (!Comparators.isInOrder(nodeKeys, Integer::compareTo)) {
            return false;
        }

        // Trivially adheres to the property if the node has no children and
        // meets the previous conditions
        if (nodeChildren.size() == 0) {
            return true;
        }

        // Iterate over the keys of the current node and test the left and
        // right nodes to make sure all keys are less than and greater than it,
        // respectively.
        int idx = 0;
        for (var key : nodeKeys) {
            var leftChild = nodeChildren.get(idx);
            var rightChild = nodeChildren.get(idx + 1);

            for (var leftChildKey : leftChild.keys) {
                if (leftChildKey.compareTo(key) > 0) {
                    return false;
                }
            }

            for (var rightChildKey : rightChild.keys) {
                if (rightChildKey.compareTo(key) < 0) {
                    return false;
                }
            }

            idx++;
        }

        return true;
    }

    boolean validateMaxDegreeProperty(BTree<Integer> btree) {
        Queue<BTreeNode<Integer>> nodeQueue = new LinkedList<>();
        nodeQueue.add(btree.root);

        while (!nodeQueue.isEmpty()) {
            var currentNode = nodeQueue.poll();

            if (currentNode.children.size() > btree.maxDegree || currentNode.keys.size() > btree.maxDegree - 1) {
                return false;
            }
        }

        return true;
    }


    @org.junit.jupiter.api.Test
    @DisplayName("1000 items inserted in ascending order for multiple degrees")
    public void ascendingItemTest() {
        for (int i = 3; i < 15; i++) {
            BTree<Integer> btree = new BTree<>(i);

            for (int j = 0; j < 1000; j++) {
                btree.insert(j);
            }

            assertTrue(validateSearchProperty(btree) && validateMaxDegreeProperty(btree));
        }
    }

    @org.junit.jupiter.api.Test
    @DisplayName("1000 items inserted in descending order for multiple degrees")
    public void descendingItemTest() {
        for (int i = 3; i < 15; i++) {
            BTree<Integer> btree = new BTree<>(i);

            for (int j = 1000; j > 0; j--) {
                btree.insert(j);
            }

            assertTrue(validateSearchProperty(btree) && validateMaxDegreeProperty(btree));
        }
    }

    @org.junit.jupiter.api.Test
    @DisplayName("1000 random values inserted for 100 different seeds and multiple degrees")
    public void randomItemTest() {
        for (int i = 0; i < 100; i++) {
            Random rand = new Random(i);

            for (int j = 3; j < 15; j++) {
                BTree<Integer> btree = new BTree<>(j);
                Set<Integer> seenItems = new HashSet<>();

                for (int k = 0; k < 1000; k++) {
                    int newRandInt = rand.nextInt(100000);

                    while (seenItems.contains(newRandInt)) {
                        newRandInt = rand.nextInt(100000);
                    }

                    btree.insert(newRandInt);
                    seenItems.add(newRandInt);
                }

                assertTrue(validateSearchProperty(btree) && validateMaxDegreeProperty(btree));
            }
        }
    }
}