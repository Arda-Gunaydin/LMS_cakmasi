public class Lab06 {
    public static void main(String[] args) {
    }
}

// Common list methods
interface List {
    boolean isEmpty();
    int size();
}

interface Stack<E> extends List {
    void push(E item);
    E pop();
    E top();
}

interface Queue<E> extends List {
    void enqueue(E item);
    E dequeue();
    E front();
}

class ArrayStack<E> implements Stack<E> {

    private static final int DEFAULT_CAPACITY = 4;
    private E[] data;
    private int top;

    @SuppressWarnings("unchecked")
    public ArrayStack() {
        data = (E[]) new Object[DEFAULT_CAPACITY];
        top = 0;
    }

    @Override
    public boolean isEmpty() {
        return top == 0;
    }

    @Override
    public int size() {
        return top;
    }

    @Override
    public void push(E item) {
        if (item == null || top == data.length) return;
        data[top++] = item;
    }

    @Override
    public E pop() {
        if (top == 0) return null;
        E e = data[--top];
        data[top] = null;
        return e;
    }

    @Override
    public E top() {
        return top == 0 ? null : data[top - 1];
    }
}

class ArrayQueue<E> implements Queue<E> {

    private static final int DEFAULT_CAPACITY = 4;
    private E[] data;
    private int front;
    private int size;

    @SuppressWarnings("unchecked")
    public ArrayQueue() {
        data = (E[]) new Object[DEFAULT_CAPACITY];
        front = 0;
        size = 0;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void enqueue(E item) {
        if (item == null || size == data.length) return;
        data[(front + size) % data.length] = item;
        size++;
    }

    @Override
    public E dequeue() {
        if (size == 0) return null;
        E e = data[front];
        data[front] = null;
        front = (front + 1) % data.length;
        size--;
        return e;
    }

    @Override
    public E front() {
        return size == 0 ? null : data[front];
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

class LinkedStack<E> implements Stack<E> {

    private Node<E> top;
    private int size;

    public LinkedStack() {
        top = null;
        size = 0;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void push(E item) {
        if (item == null) return;
        Node<E> n = new Node<>(item);
        n.next = top;
        top = n;
        size++;
    }

    @Override
    public E pop() {
        if (top == null) return null;
        E e = top.data;
        top = top.next;
        size--;
        return e;
    }

    @Override
    public E top() {
        return top == null ? null : top.data;
    }
}

class LinkedQueue<E> implements Queue<E> {

    private Node<E> front;
    private Node<E> rear;
    private int size;

    public LinkedQueue() {
        front = null;
        rear = null;
        size = 0;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void enqueue(E item) {
        if (item == null) return;
        Node<E> n = new Node<>(item);
        if (rear == null) {
            front = rear = n;
        } else {
            rear.next = n;
            rear = n;
        }
        size++;
    }

    @Override
    public E dequeue() {
        if (front == null) return null;
        E e = front.data;
        front = front.next;
        if (front == null) rear = null;
        size--;
        return e;
    }

    @Override
    public E front() {
        return front == null ? null : front.data;
    }
}
