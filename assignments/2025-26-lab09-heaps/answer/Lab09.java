import java.util.LinkedList;
import java.util.Queue;

interface Heap<E extends Comparable<E>> {

    int size();
    boolean isEmpty();
    void insert(E element);
    E min();
    E removeMin();
}

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

    @Override
    public void insert(E element) {
        if (element == null) return;
        if (size == data.length) data = java.util.Arrays.copyOf(data, data.length * 2);
        data[size] = element;
        heapUp(size);
        size++;
    }

    @Override
    public E min() {
        return size == 0 ? null : data[0];
    }

    @Override
    public E removeMin() {
        if (size == 0) return null;
        E root = data[0];
        data[0] = data[size - 1];
        data[size - 1] = null;
        size--;
        heapDown(0);
        return root;
    }

    private void heapUp(int index) {
        while (index > 0) {
            int parent = (index - 1) / 2;
            if (data[parent].compareTo(data[index]) <= 0) break;
            swap(parent, index);
            index = parent;
        }
    }

    private void heapDown(int index) {
        while (2 * index + 1 < size) {
            int child = 2 * index + 1;
            if (child + 1 < size && data[child + 1].compareTo(data[child]) < 0) child++;   // equal: keep left
            if (data[index].compareTo(data[child]) <= 0) break;
            swap(index, child);
            index = child;
        }
    }

    private void swap(int i, int j) {
        E t = data[i];
        data[i] = data[j];
        data[j] = t;
    }

    @SuppressWarnings("unchecked")
    public static <E extends Comparable<E>> ArrayHeap<E> merge(ArrayHeap<E> h1, ArrayHeap<E> h2) {
        int n1 = h1 == null ? 0 : h1.size;
        int n2 = h2 == null ? 0 : h2.size;
        ArrayHeap<E> r = new ArrayHeap<>();
        r.data = (E[]) new Comparable[Math.max(DEFAULT_CAPACITY, n1 + n2)];
        for (int i = 0; i < n1; i++) r.data[i] = h1.data[i];
        for (int i = 0; i < n2; i++) r.data[n1 + i] = h2.data[i];
        r.size = n1 + n2;
        for (int i = r.size / 2 - 1; i >= 0; i--) r.heapDown(i);   // build-heap, O(n)
        return r;
    }
}

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

    @Override
    public void insert(E element) {
        if (element == null) return;
        Node<E> n = new Node<>(element);
        size++;
        if (root == null) {
            root = n;
            return;
        }
        Queue<Node<E>> q = new LinkedList<>();
        q.add(root);
        while (true) {
            Node<E> c = q.poll();
            if (c.left == null) {
                c.left = n;
                n.parent = c;
                break;
            }
            if (c.right == null) {
                c.right = n;
                n.parent = c;
                break;
            }
            q.add(c.left);
            q.add(c.right);
        }
        heapUp(n);
    }

    @Override
    public E min() {
        return root == null ? null : root.data;
    }

    @Override
    public E removeMin() {
        if (root == null) return null;
        E m = root.data;
        if (size == 1) {
            root = null;
            size = 0;
            return m;
        }
        // 1) BFS to find the last node
        Queue<Node<E>> q = new LinkedList<>();
        q.add(root);
        Node<E> last = root;
        while (!q.isEmpty()) {
            last = q.poll();
            if (last.left != null) q.add(last.left);
            if (last.right != null) q.add(last.right);
        }
        // 3) + 4) move its value to the root and detach it
        root.data = last.data;
        if (last.parent.right == last) last.parent.right = null;
        else last.parent.left = null;
        last.parent = null;
        size--;
        heapDown(root);
        return m;
    }

    private void heapUp(Node<E> node) {
        while (node.parent != null && node.parent.data.compareTo(node.data) > 0) {
            E t = node.data;
            node.data = node.parent.data;
            node.parent.data = t;
            node = node.parent;
        }
    }

    private void heapDown(Node<E> node) {
        while (node.left != null) {
            Node<E> c = node.left;
            if (node.right != null && node.right.data.compareTo(c.data) < 0) c = node.right;
            if (node.data.compareTo(c.data) <= 0) break;
            E t = node.data;
            node.data = c.data;
            c.data = t;
            node = c;
        }
    }
}

public class Lab09 {
    public static void main(String[] args) {
    }

    public static <E extends Comparable<E>> void heapSort(E[] arr) {
        if (arr == null) return;
        int n = arr.length;
        // Step 1: build a max-heap
        for (int i = n / 2 - 1; i >= 0; i--) heapifyUp(arr, n, i);
        // Step 2: move the max to the end, then restore the reduced heap
        for (int end = n - 1; end > 0; end--) {
            swap(arr, 0, end);
            heapifyUp(arr, end, 0);
        }
    }

    // Maintain max-heap property starting at index i
    private static <E extends Comparable<E>> void heapifyUp(E[] arr, int heapSize, int i) {
        int largest = i;
        int l = 2 * i + 1;
        int r = 2 * i + 2;
        if (l < heapSize && arr[l].compareTo(arr[largest]) > 0) largest = l;
        if (r < heapSize && arr[r].compareTo(arr[largest]) > 0) largest = r;
        if (largest != i) {
            swap(arr, i, largest);
            heapifyUp(arr, heapSize, largest);
        }
    }

    // Utility swap
    private static <E> void swap(E[] arr, int i, int j) {
        E temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}
