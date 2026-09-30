/**
 * CS201 – Data Structures
 * Lab 08 – Priority Queues (last year's lab, 2025-2026)
 * 
 * DO NOT THROW EXCEPTIONS!!!  (the "Not implemented" stubs below are placeholders: replace them)
 *
 * Tasks:
 * 1) Implement an **unsorted array-based** priority queue
 * 2) Implement a **sorted linked-list-based** priority queue
 *
 * Rules:
 * - This is a **min-priority queue**
 * - `min()` returns the **smallest element**
 * - `removeMin()` removes and returns the **smallest element**
 * - Use `E extends Comparable<E>`
 */

public class Lab08 {
    public static void main(String[] args) {
        try {
            PriorityQueue<Integer> pq = new UnsortedArrayPQ<>();
            pq.insert(5);
            pq.insert(2);
            pq.insert(9);
            pq.insert(1);

            System.out.println(pq.min());       // 1
            System.out.println(pq.removeMin()); // 1
            System.out.println(pq.min());       // 2

            PriorityQueue<String> spq = new SortedLinkedPQ<>();
            spq.insert("pear");
            spq.insert("apple");
            spq.insert("fig");
            System.out.println(spq.removeMin()); // apple
            System.out.println(spq.removeMin()); // fig
        } catch (UnsupportedOperationException e) {
            System.out.println();
            System.out.println("Stopped at a method that is not implemented yet:");
            System.out.println("  " + e.getStackTrace()[0]);
        }
    }
}

// --------------------------------------------------
// Priority Queue Interface
// --------------------------------------------------
interface PriorityQueue<E extends Comparable<E>> {
    int size();        // return number of elements
    boolean isEmpty(); // return true if empty
    void insert(E element); // insert a new element
    E min();           // return smallest element (do NOT remove)
    E removeMin();     // remove and return smallest element
}

// --------------------------------------------------
// Unsorted Array-Based Priority Queue (TODO)
// --------------------------------------------------
class UnsortedArrayPQ<E extends Comparable<E>> implements PriorityQueue<E> {

    private static final int DEFAULT_CAPACITY = 16;
    private E[] data;
    private int size;

    @SuppressWarnings("unchecked")
    public UnsortedArrayPQ() {
        data = (E[]) new Comparable[DEFAULT_CAPACITY];
        size = 0;
    }


    @Override
    public int size() {
        // TODO: return number of elements
        throw new UnsupportedOperationException("Not implemented");
    }


    @Override
    public boolean isEmpty() {
        // TODO: return true if empty
        throw new UnsupportedOperationException("Not implemented");
    }


    @Override
    public void insert(E element) {
        // TODO:
        // Insert at next free position (no sorting)
        // If array is full, print "PQ is full!" and return
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * TODO:
     * Helper method: find index of smallest element
     * Scan the array and find min by using compareTo()
     */
    private int findMinIndex() {
        throw new UnsupportedOperationException("Not implemented");
    }


    @Override
    public E min() {
        // TODO:
        // Return smallest element WITHOUT removing it
        // If empty, return null
        throw new UnsupportedOperationException("Not implemented");
    }


    @Override
    public E removeMin() {
        // TODO:
        // 1) Find smallest index
        // 2) Save value to return
        // 3) Move last element into min-index slot
        // 4) Null out last element and decrease size
        // 5) Return removed element
        throw new UnsupportedOperationException("Not implemented");
    }
}

// --------------------------------------------------
// Node Class for Linked List
// --------------------------------------------------
class Node<E> {
    E data;
    Node<E> next;

    Node(E data) {
        this.data = data;
        this.next = null;
    }
}

// --------------------------------------------------
// Sorted Linked-List Priority Queue (TODO)
// --------------------------------------------------
class SortedLinkedPQ<E extends Comparable<E>> implements PriorityQueue<E> {

    private Node<E> head; // always points to smallest element
    private int size;

    public SortedLinkedPQ() {
        head = null;
        size = 0;
    }


    @Override
    public int size() {
        // TODO
        throw new UnsupportedOperationException("Not implemented");
    }


    @Override
    public boolean isEmpty() {
        // TODO
        throw new UnsupportedOperationException("Not implemented");
    }


    @Override
    public void insert(E element) {
        // TODO:
        // Insert element in sorted order (ascending)
        // Cases:
        // 1) empty list
        // 2) insert before head
        // 3) traverse until correct spot and insert
        throw new UnsupportedOperationException("Not implemented");
    }


    @Override
    public E min() {
        // TODO: return smallest element (do not remove)
        // If empty, return null
        throw new UnsupportedOperationException("Not implemented");
    }


    @Override
    public E removeMin() {
        // TODO:
        // Remove head and return it
        // If empty, return null
        throw new UnsupportedOperationException("Not implemented");
    }
}
