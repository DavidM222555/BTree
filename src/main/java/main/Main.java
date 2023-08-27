package main;

public class Main {
    public static void main(String[] args) {
        BTree<Integer> btree = new BTree<>(3);

        for (int i = 0; i < 1000; i++) {
            btree.insert(i);
        }

        boolean searchFor999Result = btree.search(999); // Expect true
        boolean searchFor1000Result = btree.search(1000); // Expect false

        System.out.println("Search for 999 result: " + searchFor999Result);
        System.out.println("Search for 1000 result: " + searchFor1000Result);
    }
}