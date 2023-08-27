# BTree
Basic B-Tree implementation in Java. Supports insertion and search on items that implement Comparable<T>. Will possibly look into supporting delete in the future.
In the tests suite I have three basic test cases (one of which consists of 100 seeded random tests) that test the search property and max-degree property for each node after a series of insertions. These tests are ran for every degree of B-Tree from 3 to 15.

# Example
```
public class Main {
    public static void main(String[] args) {
        BTree<Integer> btree = new BTree<>(3);

        for (int i = 0; i < 1000; i++) {
            btree.insert(i);
        }

        // For ease of implementation this code uses just the value itself
        // as oppossed to a key-value system which would be more practical
        // for real-life applications.
        boolean searchFor999Result = btree.search(999); // Expect true
        boolean searchFor1000Result = btree.search(1000); // Expect false

        System.out.println("Search for 999 result: " + searchFor999Result);
        System.out.println("Search for 1000 result: " + searchFor1000Result);
    }
}
```
