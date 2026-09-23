/**
 * CSE201 Lab 5 - Queues.
 *
 * Read the description before you start. Everything you write goes into this file.
 * The declarations below are the ones the description requires; fill in the bodies.
 *
 * Only Lab05 is public. Do not add a package declaration.
 * Do not use any java.util collection (both queues must be your own implementations).
 */

/** Part 1. A generic, growable, circular array queue. */
class CircularQueue<T> {

    private T[] items;
    private int front;
    private int size;

    public CircularQueue(int initialCapacity) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public CircularQueue() {
        throw new UnsupportedOperationException("Not implemented");
    }

    public void enqueue(T item) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public T dequeue() {
        throw new UnsupportedOperationException("Not implemented");
    }

    public T peek() {
        throw new UnsupportedOperationException("Not implemented");
    }

    public boolean isEmpty() {
        throw new UnsupportedOperationException("Not implemented");
    }

    public int size() {
        throw new UnsupportedOperationException("Not implemented");
    }

    public int capacity() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public String toString() {
        throw new UnsupportedOperationException("Not implemented");
    }
}

/** Part 2. A generic queue built from singly linked nodes. */
class LinkedQueue<T> {

    private class Node {
        T data;
        Node next;
    }

    private Node head;
    private Node tail;
    private int size;

    public LinkedQueue() {
        throw new UnsupportedOperationException("Not implemented");
    }

    public void enqueue(T item) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public T dequeue() {
        throw new UnsupportedOperationException("Not implemented");
    }

    public T peek() {
        throw new UnsupportedOperationException("Not implemented");
    }

    public boolean isEmpty() {
        throw new UnsupportedOperationException("Not implemented");
    }

    public int size() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public String toString() {
        throw new UnsupportedOperationException("Not implemented");
    }
}

/** Your playground for the Run button. It is not graded; change it freely. */
public class Lab05 {

    public static void main(String[] args) {
        try {
            CircularQueue<Integer> c = new CircularQueue<>(4);
            c.enqueue(10);
            c.enqueue(20);
            c.enqueue(30);
            System.out.println("circular queue        = " + c);
            System.out.println("dequeue()             = " + c.dequeue());
            System.out.println("circular after dequeue = " + c);

            LinkedQueue<String> l = new LinkedQueue<>();
            l.enqueue("a");
            l.enqueue("b");
            System.out.println("linked queue          = " + l);
            System.out.println("linked dequeue()      = " + l.dequeue());

            System.out.println();
            String[] players = {"Ada", "Bob", "Cem", "Deniz"};
            System.out.println("hotPotato(players, 3) = " + hotPotato(players, 3));
            System.out.println("generateBinary(5)     = " + String.join(", ", generateBinary(5)));
        } catch (UnsupportedOperationException e) {
            System.out.println();
            System.out.println("Stopped at a method that is not implemented yet:");
            System.out.println("  " + e.getStackTrace()[0]);
        }
    }

    /** Part 3. Josephus-style elimination game, using one of your own queues. */
    public static String hotPotato(String[] players, int k) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /** Part 3. The binary representations of 1..n, generated with one of your own queues. */
    public static String[] generateBinary(int n) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
