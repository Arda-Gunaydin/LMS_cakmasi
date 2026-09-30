/**
 * CS201 - Data Structures
 * Lab 06 - Stacks and Queues (last year's lab, 2025-2026)
 *
 * In this lab, you will implement both array-based and linked-list-based versions.
 * Implementations use fixed-size arrays (no resizing) for array-based versions.
 *
 * Read the description before you start. The declarations below are the ones the
 * description requires; fill in the bodies. Only Lab06 is public. No package declaration.
 * Do not use any java.util class.
 */

public class Lab06 {
    public static void main(String[] args) {
        try {
            Stack<Integer> s = new ArrayStack<>();
            s.push(1);
            s.push(2);
            s.push(3);
            System.out.println("ArrayStack  pop()     = " + s.pop());
            System.out.println("ArrayStack  top()     = " + s.top());
            System.out.println("ArrayStack  size()    = " + s.size());

            Queue<String> q = new ArrayQueue<>();
            q.enqueue("a");
            q.enqueue("b");
            q.enqueue("c");
            System.out.println("ArrayQueue  dequeue() = " + q.dequeue());
            System.out.println("ArrayQueue  front()   = " + q.front());

            Stack<Integer> ls = new LinkedStack<>();
            ls.push(10);
            ls.push(20);
            System.out.println("LinkedStack pop()     = " + ls.pop());

            Queue<Integer> lq = new LinkedQueue<>();
            lq.enqueue(10);
            lq.enqueue(20);
            System.out.println("LinkedQueue dequeue() = " + lq.dequeue());
        } catch (UnsupportedOperationException e) {
            System.out.println();
            System.out.println("Stopped at a method that is not implemented yet:");
            System.out.println("  " + e.getStackTrace()[0]);
        }
    }
}

// Common list methods
interface List {
    boolean isEmpty();
    int size();
}

/////////////////////////////
// Stack Interface
/////////////////////////////

interface Stack<E> extends List {
    void push(E item);
    E pop();
    E top();
}

/////////////////////////////
// Queue Interface
/////////////////////////////

interface Queue<E> extends List {
    void enqueue(E item);
    E dequeue();
    E front();
}

/////////////////////////////
// Array-Based Stack
/////////////////////////////

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
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void push(E item) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public E pop() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public E top() {
        throw new UnsupportedOperationException("Not implemented");
    }
}

/////////////////////////////
// Array-Based Queue
/////////////////////////////

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
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void enqueue(E item) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public E dequeue() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public E front() {
        throw new UnsupportedOperationException("Not implemented");
    }
}

/////////////////////////////
// Node Class (for linked list)
/////////////////////////////

class Node<E> {
    E data;
    Node<E> next;

    Node(E data) {
        this.data = data;
        this.next = null;
    }
}

/////////////////////////////
// Linked List-Based Stack
/////////////////////////////

class LinkedStack<E> implements Stack<E> {

    private Node<E> top;
    private int size;

    public LinkedStack() {
        top = null;
        size = 0;
    }

    @Override
    public boolean isEmpty() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void push(E item) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public E pop() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public E top() {
        throw new UnsupportedOperationException("Not implemented");
    }
}

/////////////////////////////
// Linked List-Based Queue
/////////////////////////////

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
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void enqueue(E item) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public E dequeue() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public E front() {
        throw new UnsupportedOperationException("Not implemented");
    }
}
