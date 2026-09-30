/**
 * CSE201 Lab 3 - Singly, Circular and Doubly Linked Lists (last year's lab, 2025-2026).
 *
 * Read the description before you start. Everything you write goes into this file.
 * The declarations below are the ones the description requires; fill in the bodies.
 *
 * Only Lab03 is public. Do not add a package declaration.
 * Do not use any java.util class: build every list from nodes.
 */
public class Lab03 {
    public static void main(String[] args) {
        try {
            MyList<Integer> s = new SinglyLinkedList<>();
            s.addLast(20);
            s.addLast(30);
            s.addFirst(10);
            s.insertAt(1, 15);
            System.out.println("Singly:   get(1) = " + s.get(1) + ", size = " + s.size() + ", indexOf(30) = " + s.indexOf(30));
            System.out.println("Singly:   removeLast() = " + s.removeLast());

            MyList<String> c = new CircularLinkedList<>();
            c.addLast("b");
            c.addFirst("a");
            c.addLast("c");
            System.out.println("Circular: get(0) = " + c.get(0) + ", removeFirst() = " + c.removeFirst());

            MyList<Integer> d = new DoublyLinkedList<>();
            d.addLast(1);
            d.addLast(2);
            d.addLast(3);
            System.out.println("Doubly:   removeAt(1) = " + d.removeAt(1) + ", remove(3) = " + d.remove(3));
        } catch (UnsupportedOperationException e) {
            System.out.println();
            System.out.println("Stopped at a method that is not implemented yet:");
            System.out.println("  " + e.getStackTrace()[0]);
        }
    }
}

interface MyList<E> {

    /**
     * Returns the number of elements in the list.
     */
    int size();

    /**
     * Returns true if the list is empty.
     */
    boolean isEmpty();

    /**
     * Removes all elements from the list.
     */
    void clear();

    /**
     * Inserts an element at the beginning of the list.
     */
    void addFirst(E e);

    /**
     * Inserts an element at the end of the list.
     */
    void addLast(E e);

    /**
     * Inserts an element at a given index (0..size).
     * Does nothing if the index is invalid.
     */
    void insertAt(int index, E e);

    /**
     * Removes and returns the first element, or null if empty.
     */
    E removeFirst();

    /**
     * Removes and returns the last element, or null if empty.
     */
    E removeLast();

    /**
     * Removes and returns the element at a given index.
     * Returns null if index is invalid.
     */
    E removeAt(int index);

    /**
     * Returns the element at a given index, or null if invalid.
     */
    E get(int index);

    /**
     * Replaces the element at a given index with a new value.
     * Returns the old value, or null if index is invalid.
     */
    E set(int index, E e);

    /**
     * Returns the index of the first occurrence of the given object,
     * or -1 if not found.
     */
    int indexOf(E o);

    /**
     * Removes the first occurrence of the given object.
     * Returns true if removed, false otherwise.
     */
    boolean remove(E o);
}


class Node<E> {
    public E item;
    public Node<E> next;

    public Node(E item, Node<E> next) {
        this.item = item;
        this.next = next;
    }
}

/** Part 1. head -> ... -> tail -> null */
class SinglyLinkedList<E> implements MyList<E> {

    private Node<E> head;
    private Node<E> tail;
    private int size;

    public Node<E> getHead() {
        // Do not modify this method
        return head;
    }

    public Node<E> getTail() {
        // Do not modify this method
        return tail;
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean isEmpty() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void addFirst(E e) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void addLast(E e) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void insertAt(int index, E e) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public E removeFirst() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public E removeLast() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public E removeAt(int index) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public E get(int index) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public E set(int index, E e) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public int indexOf(E o) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean remove(E o) {
        throw new UnsupportedOperationException("Not implemented");
    }
}

/** Part 2. head -> ... -> tail -> head (the last node points back to the first one). */
class CircularLinkedList<E> implements MyList<E> {

    private Node<E> head;
    private Node<E> tail;
    private int size;

    public CircularLinkedList() {
    }

    public Node<E> getTail() {
        // Do not modify this method
        return tail;
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean isEmpty() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void addFirst(E e) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void addLast(E e) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void insertAt(int index, E e) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public E removeFirst() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public E removeLast() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public E removeAt(int index) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public E get(int index) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public E set(int index, E e) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public int indexOf(E o) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean remove(E o) {
        throw new UnsupportedOperationException("Not implemented");
    }
}

class DNode<E> {
    public E item;
    public DNode<E> prev;
    public DNode<E> next;

    public DNode(E item, DNode<E> prev, DNode<E> next) {
        this.item = item;
        this.prev = prev;
        this.next = next;
    }
}

/** Part 3. null <- head <-> ... <-> tail -> null */
class DoublyLinkedList<E> implements MyList<E> {

    private DNode<E> head;
    private DNode<E> tail;
    private int size;

    public DNode<E> getHead() {
        // Do not modify this method
        return head;
    }

    public DNode<E> getTail() {
        // Do not modify this method
        return tail;
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean isEmpty() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void addFirst(E e) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void addLast(E e) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void insertAt(int index, E e) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public E removeFirst() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public E removeLast() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public E removeAt(int index) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public E get(int index) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public E set(int index, E e) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public int indexOf(E o) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean remove(E o) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
