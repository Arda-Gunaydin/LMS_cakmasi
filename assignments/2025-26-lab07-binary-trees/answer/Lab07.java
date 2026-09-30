import java.util.List;
import java.util.Queue;
import java.util.Stack;
import java.util.LinkedList;

public class Lab07 {
    public static void main(String[] args) {
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

class ArrayBT<E> implements ITree<E> {

    private E[] data;
    private int size;

    @SuppressWarnings("unchecked")
    public ArrayBT(int capacity) {
        data = (E[]) new Object[capacity];
        size = 0;
    }

    // ---------- Recursive traversal helpers ----------

    private void recInorder(int index, List<E> list) {
        if (index >= size) return;
        recInorder(2 * index + 1, list);
        list.add(data[index]);
        recInorder(2 * index + 2, list);
    }

    private void recPreorder(int index, List<E> list) {
        if (index >= size) return;
        list.add(data[index]);
        recPreorder(2 * index + 1, list);
        recPreorder(2 * index + 2, list);
    }

    private void recPostorder(int index, List<E> list) {
        if (index >= size) return;
        recPostorder(2 * index + 1, list);
        recPostorder(2 * index + 2, list);
        list.add(data[index]);
    }

    // ---------- ITree<E> methods ----------

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
        if (element == null || size == data.length) return;
        data[size++] = element;
    }

    private int indexOf(E element) {
        for (int i = 0; i < size; i++) {
            if (data[i].equals(element)) return i;
        }
        return -1;
    }

    @Override
    public boolean remove(E element) {
        if (element == null) return false;
        int i = indexOf(element);
        if (i < 0) return false;
        data[i] = data[size - 1];
        data[size - 1] = null;
        size--;
        return true;
    }

    @Override
    public boolean contains(E element) {
        return element != null && indexOf(element) >= 0;
    }

    @Override
    public void BFS(List<E> list) {
        for (int i = 0; i < size; i++) list.add(data[i]);
    }

    @Override
    public void DFS(List<E> list) {
        preorder(list);
    }

    @Override
    public void inorder(List<E> list) {
        recInorder(0, list);
    }

    @Override
    public void preorder(List<E> list) {
        recPreorder(0, list);
    }

    @Override
    public void postorder(List<E> list) {
        recPostorder(0, list);
    }
}

class LinkedBT<E> implements ITree<E> {

    private Node<E> root;
    private int size;

    public LinkedBT() {
        root = null;
        size = 0;
    }

    // ---------- Recursive traversal helpers ----------

    private void recInorder(Node<E> node, List<E> list) {
        if (node == null) return;
        recInorder(node.left, list);
        list.add(node.data);
        recInorder(node.right, list);
    }

    private void recPreorder(Node<E> node, List<E> list) {
        if (node == null) return;
        list.add(node.data);
        recPreorder(node.left, list);
        recPreorder(node.right, list);
    }

    private void recPostorder(Node<E> node, List<E> list) {
        if (node == null) return;
        recPostorder(node.left, list);
        recPostorder(node.right, list);
        list.add(node.data);
    }

    // ---------- ITree<E> methods ----------

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
        while (!q.isEmpty()) {
            Node<E> c = q.poll();
            if (c.left == null) {
                c.left = n;
                return;
            }
            if (c.right == null) {
                c.right = n;
                return;
            }
            q.add(c.left);
            q.add(c.right);
        }
    }

    /** BFS for the first node holding element, or null. */
    private Node<E> find(E element) {
        if (root == null) return null;
        Queue<Node<E>> q = new LinkedList<>();
        q.add(root);
        while (!q.isEmpty()) {
            Node<E> c = q.poll();
            if (c.data.equals(element)) return c;
            if (c.left != null) q.add(c.left);
            if (c.right != null) q.add(c.right);
        }
        return null;
    }

    @Override
    public boolean remove(E element) {
        if (element == null) return false;
        Node<E> target = find(element);
        if (target == null) return false;
        if (size == 1) {
            root = null;
            size = 0;
            return true;
        }
        // BFS to the last node, remembering its parent
        Queue<Node<E>> q = new LinkedList<>();
        q.add(root);
        Node<E> last = null;
        Node<E> parent = null;
        while (!q.isEmpty()) {
            Node<E> c = q.poll();
            if (c.left != null) {
                parent = c;
                last = c.left;
                q.add(c.left);
            }
            if (c.right != null) {
                parent = c;
                last = c.right;
                q.add(c.right);
            }
        }
        target.data = last.data;
        if (parent.right == last) parent.right = null;
        else parent.left = null;
        size--;
        return true;
    }

    @Override
    public boolean contains(E element) {
        return element != null && find(element) != null;
    }

    @Override
    public void BFS(List<E> list) {
        if (root == null) return;
        Queue<Node<E>> q = new LinkedList<>();
        q.add(root);
        while (!q.isEmpty()) {
            Node<E> c = q.poll();
            list.add(c.data);
            if (c.left != null) q.add(c.left);
            if (c.right != null) q.add(c.right);
        }
    }

    @Override
    public void DFS(List<E> list) {
        if (root == null) return;
        Stack<Node<E>> s = new Stack<>();
        s.push(root);
        while (!s.isEmpty()) {
            Node<E> c = s.pop();
            list.add(c.data);
            if (c.right != null) s.push(c.right);   // right first, so left is popped first
            if (c.left != null) s.push(c.left);
        }
    }

    @Override
    public void inorder(List<E> list) {
        recInorder(root, list);
    }

    @Override
    public void preorder(List<E> list) {
        recPreorder(root, list);
    }

    @Override
    public void postorder(List<E> list) {
        recPostorder(root, list);
    }
}
