import java.util.LinkedList;
import java.util.Queue;

/**
 * CS201 – Data Structures
 * Lab 09 – Heaps (Array vs Node Implementations) (last year's lab, 2025-2026)
 *
 * In this lab, you will:
 *  - Implement an array-based min-heap
 *  - Implement a node-based min-heap
 *  - Compare their insertion and removal complexities
 *  - Understand why heaps are almost always implemented using arrays
 *
 * Complete all TODO sections.
 */


// --------------------------------------------------
// Heap Interface (Min-Heap)
// --------------------------------------------------
interface Heap<E extends Comparable<E>> {

    int size();
    boolean isEmpty();
    void insert(E element);
    E min();
    E removeMin();
}

// --------------------------------------------------
// Array-Based Min-Heap (Student Skeleton)
// --------------------------------------------------
class ArrayHeap<E extends Comparable<E>> implements Heap<E> {

    private static final int DEFAULT_CAPACITY = 64;

    private E[] data;
    private int size;

    @SuppressWarnings("unchecked")
    public ArrayHeap() {
        data = (E[]) new Comparable[DEFAULT_CAPACITY];
        size = 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Insert element at the end of the array, then restore heap property using heapUp.
     */
    @Override
    public void insert(E element) {

        // TODO: 1) Insert at data[size]
        // TODO: 2) Call heapUp(size)
        // TODO: 3) Increment size
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public E min() {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Remove root (min element), replace with last element, then restore heap using heapDown.
     */
    @Override
    public E removeMin() {

        // TODO: handle empty heap case
        // TODO: save root value
        // TODO: move last element to root
        // TODO: decrement size
        // TODO: heapDown(0)
        // TODO: return removed root

        throw new UnsupportedOperationException("Not implemented");
    }

    // -----------------------------
    // Heap-Up (Bubble-Up)
    // -----------------------------
    private void heapUp(int index) {

        // TODO:
        // while index > 0 and parent > child:
        //     swap
        //     index = parent
        throw new UnsupportedOperationException("Not implemented");
    }

    // -----------------------------
    // Heap-Down (Bubble-Down)
    // -----------------------------
    private void heapDown(int index) {

        // TODO:
        // while left child exists:
        //    determine smaller child
        //    if data[index] <= data[smaller child], stop
        //    else swap and continue downward
        throw new UnsupportedOperationException("Not implemented");
    }

    // -----------------------------
    // Utility: swap elements in array
    // -----------------------------
    private void swap(int i, int j) {

        // TODO: basic array element swap
        throw new UnsupportedOperationException("Not implemented");
    }

    // --------------------------------------------------
    // Static Merge: Create a new heap from two heaps
    // --------------------------------------------------
    public static <E extends Comparable<E>> ArrayHeap<E> merge(
            ArrayHeap<E> h1, ArrayHeap<E> h2) {

        // TODO:
        // copy both arrays into a bigger array,
        // then perform heapDown on all non-leaf nodes (build-heap O(n))
        //
        // Return the new merged heap

        throw new UnsupportedOperationException("Not implemented");
    }
}

// --------------------------------------------------
// Node class for LinkedHeap
// --------------------------------------------------
class Node<E> {

    E data;
    Node<E> left;
    Node<E> right;
    Node<E> parent;

    Node(E data) {
        this.data = data;
        this.left = null;
        this.right = null;
        this.parent = null;
    }
}

// --------------------------------------------------
// Node-Based Min-Heap (Educational; Less Efficient)
// --------------------------------------------------
class LinkedHeap<E extends Comparable<E>> implements Heap<E> {

    private Node<E> root;
    private int size;

    public LinkedHeap() {
        root = null;
        size = 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Insert using BFS to find the first available position.
     * Then heapUp from the inserted node.
     */
    @Override
    public void insert(E element) {

        // Special case: empty heap

        // TODO:
        // BFS to find first node with an empty left or right
        // Insert new node
        // Set its parent
        // Call heapUp(newNode)
        // Increment size
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public E min() {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Remove root by replacing it with the last node (found by BFS).
     * Then heapDown from the new root.
     */
    @Override
    public E removeMin() {
        
        // Special case: empty heap

        // TODO:
        // 1) BFS to find last node
        // 2) Save root value
        // 3) Replace root.data with last node's data
        // 4) Remove last node from tree
        // 5) heapDown(root)
        // 6) Return saved root value

        throw new UnsupportedOperationException("Not implemented");
    }

    // -----------------------------
    // Heap-Up for LinkedHeap
    // -----------------------------
    private void heapUp(Node<E> node) {

        // TODO:
        // while node.parent != null and parent > node:
        //     swap values
        //     node = node.parent
        throw new UnsupportedOperationException("Not implemented");
    }

    // -----------------------------
    // Heap-Down for LinkedHeap
    // -----------------------------
    private void heapDown(Node<E> node) {

        // TODO:
        // while node has a child:
        //     pick smaller child
        //     if node <= smaller child, break
        //     else swap and continue
        throw new UnsupportedOperationException("Not implemented");
    }
}

public class Lab09 {
    public static void main(String[] args) {
        // You may test your heaps here manually.
        try {
            Heap<Integer> h = new ArrayHeap<>();
            h.insert(5);
            h.insert(3);
            h.insert(8);
            h.insert(1);
            System.out.println("ArrayHeap  min()       = " + h.min());         // 1
            System.out.println("ArrayHeap  removeMin() = " + h.removeMin());   // 1
            System.out.println("ArrayHeap  removeMin() = " + h.removeMin());   // 3

            Heap<Integer> lh = new LinkedHeap<>();
            lh.insert(4);
            lh.insert(2);
            lh.insert(6);
            System.out.println("LinkedHeap removeMin() = " + lh.removeMin());  // 2

            Integer[] arr = {9, 4, 7, 1, 8};
            heapSort(arr);
            System.out.println("heapSort              = " + java.util.Arrays.toString(arr));   // [1, 4, 7, 8, 9]
        } catch (UnsupportedOperationException e) {
            System.out.println();
            System.out.println("Stopped at a method that is not implemented yet:");
            System.out.println("  " + e.getStackTrace()[0]);
        }
    }

    // ----------------------------------------------------
    // In-place array-based heap sort (classic implementation) Do these after you implement above classes
    // ----------------------------------------------------
    public static <E extends Comparable<E>> void heapSort(E[] arr) {
        // Step 1: Build max-heap
        // Step 2: Extract elements from heap one by one
            // Move max to the end
            // Restore heap property for reduced heap
        throw new UnsupportedOperationException("Not implemented");
    }

    // Maintain max-heap property starting at index i
    private static <E extends Comparable<E>> void heapifyUp(E[] arr, int heapSize, int i) {
        // Check if left child is larger
        // Check if right child is larger
        // If root is not largest, swap & heapify subtree
        throw new UnsupportedOperationException("Not implemented");
    }

    // Utility swap
    private static <E> void swap(E[] arr, int i, int j) {
        E temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}
