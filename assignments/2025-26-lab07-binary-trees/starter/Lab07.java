import java.util.List;
import java.util.Queue;
import java.util.Stack;
import java.util.LinkedList;

/**
 * CS201 – Data Structures
 * Lab 07 – Binary Trees (last year's lab, 2025-2026)
 *
 * DO NOT THROW EXCEPTIONS!!!  (the "Not implemented" stubs below are placeholders: replace them)
 *
 * Tasks:
 * 1) Implement a Binary Tree using an array
 * 2) Implement a Binary Tree using nodes (linked structure)
 * 3) Implement traversal methods:
 *    - Preorder  (root → left → right)
 *    - Inorder   (left → root → right)
 *    - Postorder (left → right → root)
 *    - Breadth-first traversal (using Queue)
 *    - Depth-first traversal   (using Stack)
 *
 * NOTE:
 *  - Follow COMPLETE BINARY TREE rules (not BST rules)
 *  - Fill tree level-by-level (like a heap, but no heap-order)
 *  - Removal replaces target node’s value with last inserted node’s value
 */

public class Lab07 {
    public static void main(String[] args) {
        try {
            ITree<Integer> t = new ArrayBT<>(10);
            for (int i = 1; i <= 6; i++) {
                t.insert(i);
            }
            List<Integer> list = new LinkedList<>();
            t.preorder(list);
            System.out.println("ArrayBT  preorder  = " + list);   // expected: [1, 2, 4, 5, 3, 6]

            ITree<Integer> lt = new LinkedBT<>();
            for (int i = 1; i <= 6; i++) {
                lt.insert(i);
            }
            list = new LinkedList<>();
            lt.inorder(list);
            System.out.println("LinkedBT inorder   = " + list);   // expected: [4, 2, 5, 1, 6, 3]
            list = new LinkedList<>();
            lt.BFS(list);
            System.out.println("LinkedBT BFS       = " + list);   // expected: [1, 2, 3, 4, 5, 6]
        } catch (UnsupportedOperationException e) {
            System.out.println();
            System.out.println("Stopped at a method that is not implemented yet:");
            System.out.println("  " + e.getStackTrace()[0]);
        }
    }
}

interface ITree<E> {
    int size();
    boolean isEmpty();
    void insert(E element);
    boolean remove(E element);
    boolean contains(E element);

    void BFS(List<E> list);   // Breadth-first
    void DFS(List<E> list);   // Depth-first with Stack

    void inorder(List<E> list);
    void preorder(List<E> list);
    void postorder(List<E> list);
}

// =========================================================
// Node class for Linked-Binary-Tree
// =========================================================

class Node<E> {
    E data;
    Node<E> left;
    Node<E> right;

    Node(E data) {
        this.data = data;
        this.left = null;
        this.right = null;
    }
}

// =========================================================
// Array-Based Binary Tree (Complete Tree)
// =========================================================

class ArrayBT<E> implements ITree<E> {

    /**
     * Array-based Complete Binary Tree
     *
     * Index rules:
     *   leftChild(i)  = 2 * i + 1
     *   rightChild(i) = 2 * i + 2
     *
     * Insert:
     *   - Place element at data[size]
     *   - size++
     *
     * Remove:
     *   - Find target index
     *   - Replace with last element:  data[target] = data[size-1]
     *   - null out last slot, size--
     *
     * Traversals:
     *   - Inorder / Preorder / Postorder must use recursive helpers below
     *
     * BFS:
     *   - Simply loop i = 0 → size-1  (array already level order)
     *
     * DFS:
     *   - You MAY just call preorder(list) here
     *     (Stack-based DFS optional for array version)
     */

    private E[] data;
    private int size;

    @SuppressWarnings("unchecked")
    public ArrayBT(int capacity) {
        data = (E[]) new Object[capacity];
        size = 0;
    }

    // ---------- Recursive traversal helpers ----------

    private void recInorder(int index, List<E> list) {
        // TODO: left → root → right
        throw new UnsupportedOperationException("Not implemented");
    }

    private void recPreorder(int index, List<E> list) {
        // TODO: root → left → right
        throw new UnsupportedOperationException("Not implemented");
    }

    private void recPostorder(int index, List<E> list) {
        // TODO: left → right → root
        throw new UnsupportedOperationException("Not implemented");
    }

    // ---------- Implement ITree<E> methods ----------

    @Override
    public int size() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean isEmpty() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void insert(E element) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean remove(E element) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean contains(E element) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void BFS(List<E> list) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void DFS(List<E> list) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void inorder(List<E> list) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void preorder(List<E> list) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void postorder(List<E> list) {
        throw new UnsupportedOperationException("Not implemented");
    }
}

// =========================================================
// Linked-Binary-Tree (Complete Tree)
// =========================================================

class LinkedBT<E> implements ITree<E> {

    /**
     * Linked Complete Binary Tree
     *
     * Insert:
     *   - Use BFS to find first missing left or right child
     *   - Insert node there
     *
     * Remove:
     *   - BFS to find node to remove
     *   - BFS to locate last inserted node
     *   - Replace target.data with last.data
     *   - Remove last node
     *
     * Traversals:
     *   - Inorder / Preorder / Postorder: use recursion (helpers below)
     *
     * BFS:
     *   - MUST use Queue<Node<E>>
     *
     * DFS (preorder):
     *   - MUST use Stack<Node<E>>
     *   - DO NOT call recursive preorder here
     */

    private Node<E> root;
    private int size;

    public LinkedBT() {
        root = null;
        size = 0;
    }

    // ---------- Recursive traversal helpers ----------

    private void recInorder(Node<E> node, List<E> list) {
        // TODO: left → root → right
        throw new UnsupportedOperationException("Not implemented");
    }

    private void recPreorder(Node<E> node, List<E> list) {
        // TODO: root → left → right
        throw new UnsupportedOperationException("Not implemented");
    }

    private void recPostorder(Node<E> node, List<E> list) {
        // TODO: left → right → root
        throw new UnsupportedOperationException("Not implemented");
    }

    // ---------- Implement ITree<E> methods ----------

    @Override
    public int size() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean isEmpty() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void insert(E element) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean remove(E element) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean contains(E element) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void BFS(List<E> list) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void DFS(List<E> list) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void inorder(List<E> list) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void preorder(List<E> list) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void postorder(List<E> list) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
