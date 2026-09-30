public class Lab08 {
    public static void main(String[] args) {
        PriorityQueue<Integer> pq = new UnsortedArrayPQ<>();
        pq.insert(5);
        pq.insert(2);
        pq.insert(9);
        pq.insert(1);

        System.out.println(pq.min());       // 1
        System.out.println(pq.removeMin()); // 1
        System.out.println(pq.min());       // 2
    }
}

interface PriorityQueue<E extends Comparable<E>> {
    int size();        // return number of elements
    boolean isEmpty(); // return true if empty
    void insert(E element); // insert a new element
    E min();           // return smallest element (do NOT remove)
    E removeMin();     // remove and return smallest element
}

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
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void insert(E element) {
        if (element == null) return;
        if (size == data.length) {
            System.out.println("PQ is full!");
            return;
        }
        data[size++] = element;
    }

    /** Index of the smallest element (the lowest index among equals), or -1 when empty. */
    private int findMinIndex() {
        if (size == 0) return -1;
        int m = 0;
        for (int i = 1; i < size; i++) {
            if (data[i].compareTo(data[m]) < 0) m = i;
        }
        return m;
    }

    @Override
    public E min() {
        int i = findMinIndex();
        return i < 0 ? null : data[i];
    }

    @Override
    public E removeMin() {
        int i = findMinIndex();
        if (i < 0) return null;
        E e = data[i];
        data[i] = data[size - 1];   // move the last element into the hole
        data[size - 1] = null;
        size--;
        return e;
    }
}

class Node<E> {
    E data;
    Node<E> next;

    Node(E data) {
        this.data = data;
        this.next = null;
    }
}

class SortedLinkedPQ<E extends Comparable<E>> implements PriorityQueue<E> {

    private Node<E> head; // always points to smallest element
    private int size;

    public SortedLinkedPQ() {
        head = null;
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

    @Override
    public void insert(E element) {
        if (element == null) return;
        Node<E> n = new Node<>(element);
        if (head == null || element.compareTo(head.data) < 0) {
            // 1) empty list, 2) insert before head
            n.next = head;
            head = n;
        } else {
            // 3) walk past every element <= element (equal elements stay first in, first out)
            Node<E> c = head;
            while (c.next != null && c.next.data.compareTo(element) <= 0) c = c.next;
            n.next = c.next;
            c.next = n;
        }
        size++;
    }

    @Override
    public E min() {
        return head == null ? null : head.data;
    }

    @Override
    public E removeMin() {
        if (head == null) return null;
        E e = head.data;
        head = head.next;
        size--;
        return e;
    }
}
