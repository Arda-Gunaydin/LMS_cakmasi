/**
 * CSE201 Lab 4 - Linked Lists.
 *
 * Read the description before you start. Everything you write goes into this file.
 * The declarations below are the ones the description requires; fill in the bodies.
 *
 * Only Lab04 is public. Do not add a package declaration.
 * Do not use any java.util collection (SinglyLinkedList must be your own node-based implementation).
 */

/** Part 1. A generic singly linked list with a head and a tail reference. */
class SinglyLinkedList<T> {

    private static class Node<T> {
        T item;
        Node<T> next;

        Node(T item) {
            this.item = item;
        }
    }

    private Node<T> head;
    private Node<T> tail;
    private int size;

    public SinglyLinkedList() {
        throw new UnsupportedOperationException("Not implemented");
    }

    public void addFirst(T item) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public void addLast(T item) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public void add(int index, T item) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public T getFirst() {
        throw new UnsupportedOperationException("Not implemented");
    }

    public T get(int index) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public T set(int index, T item) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public T removeFirst() {
        throw new UnsupportedOperationException("Not implemented");
    }

    public T removeLast() {
        throw new UnsupportedOperationException("Not implemented");
    }

    public T remove(int index) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public int indexOf(Object item) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public boolean contains(Object item) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public int size() {
        throw new UnsupportedOperationException("Not implemented");
    }

    public boolean isEmpty() {
        throw new UnsupportedOperationException("Not implemented");
    }

    public void reverse() {
        throw new UnsupportedOperationException("Not implemented");
    }

    public int removeAll(T item) {
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
            SinglyLinkedList<Integer> list = new SinglyLinkedList<>();
            list.addLast(20);
            list.addLast(30);
            list.addFirst(10);
            list.add(1, 15);
            System.out.println("list                 = " + list);
            System.out.println("get(2)               = " + list.get(2));
            System.out.println("indexOf(30)          = " + list.indexOf(30));
            System.out.println("removeFirst()        = " + list.removeFirst());
            list.reverse();
            System.out.println("after reverse()      = " + list);

            SinglyLinkedList<Integer> a = new SinglyLinkedList<>();
            SinglyLinkedList<Integer> b = new SinglyLinkedList<>();
            a.addLast(1);
            a.addLast(4);
            b.addLast(2);
            b.addLast(3);
            System.out.println("mergeSorted([1, 4], [2, 3]) = " + mergeSorted(a, b));
        } catch (UnsupportedOperationException e) {
            System.out.println();
            System.out.println("Stopped at a method that is not implemented yet:");
            System.out.println("  " + e.getStackTrace()[0]);
        }
    }

    /** Part 2. Merges two sorted lists into a new sorted list; the inputs stay unchanged. */
    public static SinglyLinkedList<Integer> mergeSorted(SinglyLinkedList<Integer> a, SinglyLinkedList<Integer> b) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
