/**
 * CSE201 Lab 4 - Binary Search Trees.
 *
 * Read the description before you start. Everything you write goes into this file.
 * The declarations below are the ones the description requires; fill in the bodies.
 *
 * Only Lab04 is public. Do not add a package declaration.
 * Do not use any java.util collection (BST must be your own node-based implementation).
 */

/** Part 1. A generic binary search tree, ordered with T.compareTo. */
class BST<T extends Comparable<T>> {

    public BST() {
        throw new UnsupportedOperationException("Not implemented");
    }

    public boolean insert(T value) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public boolean contains(T value) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public boolean remove(T value) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public int size() {
        throw new UnsupportedOperationException("Not implemented");
    }

    public int height() {
        throw new UnsupportedOperationException("Not implemented");
    }

    public boolean isEmpty() {
        throw new UnsupportedOperationException("Not implemented");
    }

    public T min() {
        throw new UnsupportedOperationException("Not implemented");
    }

    public T max() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public String toString() {
        throw new UnsupportedOperationException("Not implemented");
    }
}

/** Your playground for the Run button. It is not graded; change it freely. */
public class Lab04 {

    public static void main(String[] args) {
        try {
            BST<Integer> t = new BST<>();
            t.insert(5);
            t.insert(2);
            t.insert(8);
            t.insert(1);
            System.out.println("tree                 = " + t);
            System.out.println("size, height         = " + t.size() + ", " + t.height());
            System.out.println("contains(8)          = " + t.contains(8));
            System.out.println("min(), max()         = " + t.min() + ", " + t.max());
            t.remove(2);
            System.out.println("after remove(2)      = " + t);
        } catch (UnsupportedOperationException e) {
            System.out.println();
            System.out.println("Stopped at a method that is not implemented yet:");
            System.out.println("  " + e.getStackTrace()[0]);
        }
    }
}
