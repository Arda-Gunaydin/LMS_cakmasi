import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * CS201 – Data Structures
 * Lab 11 – Red-Black Trees
 *
 * In this lab, you will:
 * - Implement a Red-Black Tree
 * - Maintain all RB Tree insertion properties
 * - Implement recoloring and rotations
 * - Ensure the tree remains balanced after each insertion
 *
 */

public class Lab11 {
    public static void main(String[] args) {
        RBTree<Integer> tree = new RBTree<>();
        /* tree.insert(33);
        tree.insert(13);
        tree.insert(53);
        tree.insert(11);
        tree.insert(21);
        tree.insert(41);
        tree.insert(61);
        tree.insert(15);
        tree.insert(31); */

        /* tree.insert(50);
        tree.insert(20);
        tree.insert(10); */

        /* tree.insert(36);
        tree.insert(15);
        tree.insert(50);
        tree.insert(70);
        tree.insert(5);
        tree.insert(30);
        tree.insert(3);
        tree.insert(6);
        tree.insert(23);
        tree.insert(33);
        tree.insert(32); */
        System.out.println(tree);

        // You may test manually here if you want.
        // Official testing will be done using JUnit tests.
    }
}

class Node<E> {
    E data;
    Node<E> left, right, parent;
    boolean color = true;   // true = RED, false = BLACK

    public Node(E data) {
        this.data = data;
        this.color = true;  // new nodes start red
    }

    @Override
    public String toString() {
        return String.format("[%s|%s]", data, color ? "R" : "B");
    }
}

interface IList<E> {
    boolean isEmpty();
    int size();
}

interface ITree<E> extends IList<E> {
    void insert(E data);
    boolean contains(E data);
    //void remove(E data);
}

interface IBalancedTree<E> extends ITree<E> {
    Node<E> rotateRight(Node<E> node);
    Node<E> rotateLeft(Node<E> node);
}

interface IRBTree<E> extends IBalancedTree<E> {
    void check(Node<E> node);
    Node<E> balance(Node<E> node);
    void recolor(Node<E> node);
}

class RBTree<E extends Comparable<E>> implements IRBTree<E> {
    private Node<E> root;
    private int size;

    public RBTree() {}

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Node<E> rotateLeft(Node<E> node) {
        if (node == null || node.right == null) return node;
        Node<E> x = node.right;                      // 1. the right child
        node.right = x.left;                         // 2. its left subtree moves under node
        if (x.left != null) x.left.parent = node;    // 3.
        x.parent = node.parent;                      // 4. link x to node's old parent
        if (node.parent == null) root = x;
        else if (node == node.parent.left) node.parent.left = x;
        else node.parent.right = x;
        x.left = node;                               // 5.
        node.parent = x;                             // 6.
        return x;
    }

    @Override
    public Node<E> rotateRight(Node<E> node) {
        if (node == null || node.left == null) return node;
        Node<E> x = node.left;
        node.left = x.right;
        if (x.right != null) x.right.parent = node;
        x.parent = node.parent;
        if (node.parent == null) root = x;
        else if (node == node.parent.left) node.parent.left = x;
        else node.parent.right = x;
        x.right = node;
        node.parent = x;
        return x;
    }

    private boolean isRed(Node<E> node) {
        return node != null && node.color;
    }

    private Node<E> getSibling(Node<E> node) {
        if (node == null || node.parent == null) return null;
        return node == node.parent.left ? node.parent.right : node.parent.left;
    }

    @Override
    public void insert(E data) {
        if (data == null) return;
        if (root == null) {
            root = new Node<>(data);
            size++;
        } else {
            insertRec(root, data);
        }
        root.color = false;   // the root is always black
    }

    private void insertRec(Node<E> node, E data) {
        int c = data.compareTo(node.data);
        if (c == 0) return;   // no duplicates
        if (c < 0) {
            if (node.left == null) {
                Node<E> n = new Node<>(data);
                node.left = n;
                n.parent = node;
                size++;
                check(n);
            } else {
                insertRec(node.left, data);
            }
        } else {
            if (node.right == null) {
                Node<E> n = new Node<>(data);
                node.right = n;
                n.parent = node;
                size++;
                check(n);
            } else {
                insertRec(node.right, data);
            }
        }
    }

    @Override
    public void check(Node<E> newNode) {
        if (newNode == null) return;
        if (newNode == root) {
            newNode.color = false;
            return;
        }
        Node<E> parent = newNode.parent;
        if (!isRed(newNode) || !isRed(parent)) return;   // no red-red violation
        Node<E> grandParent = parent.parent;
        Node<E> uncle = getSibling(parent);
        if (isRed(uncle)) {          // Case 1
            recolor(grandParent);
            check(grandParent);
        } else {                     // Case 2 / 3
            balance(newNode);
        }
    }

    @Override
    public void recolor(Node<E> grandParent) {
        if (grandParent != root) grandParent.color = true;
        if (grandParent.left != null) grandParent.left.color = false;
        if (grandParent.right != null) grandParent.right.color = false;
    }

    @Override
    public Node<E> balance(Node<E> newNode) {
        Node<E> parent = newNode.parent;
        Node<E> grandParent = parent.parent;
        Node<E> top;
        if (parent == grandParent.left) {
            if (newNode == parent.right) rotateLeft(parent);   // LR
            top = rotateRight(grandParent);                      // LL
        } else {
            if (newNode == parent.left) rotateRight(parent);   // RL
            top = rotateLeft(grandParent);                       // RR
        }
        top.color = false;
        grandParent.color = true;
        return top;
    }

    @Override
    public boolean contains(E data) {
        return containsRec(root, data) != null;
    }

    private Node<E> containsRec(Node<E> node, E data) {
        if (node == null || data == null) return null;
        int c = data.compareTo(node.data);
        if (c == 0) return node;
        return c < 0 ? containsRec(node.left, data) : containsRec(node.right, data);
    }

    /* public void remove(E data) {
        // Optional 
    }

    private Node<E> findMin(Node<E> node) {
        // Helper for remove
        return null;
    } */

    public void BFS(List<Node<E>> list) {
        Queue<Node<E>> q = new LinkedList<>();
        q.offer(root);

        while(!q.isEmpty()) {
            Node<E> current = q.poll();
            list.add(current);
            if (current != null) {
                q.offer(current.left);
                q.offer(current.right);
            }
        }
    }

    @Override
    public String toString() {
        LinkedList<Node<E>> list = new LinkedList<>();
        this.BFS(list);

        StringBuilder sbNodes = new StringBuilder();
        StringBuilder sbIndexes = new StringBuilder();

        for (int i = 0; i < list.size(); i++) {
            Node<E> node = list.get(i);

            String nodeStr = (node == null) ? "[null|B]" : node.toString();
            String indexStr = String.valueOf(i);
            int columnWidth = Math.max(nodeStr.length(), indexStr.length()) + 2;
            String formatSpecifier = "%-" + columnWidth + "s";
            
            sbNodes.append(String.format(formatSpecifier, nodeStr));
            sbIndexes.append(String.format(formatSpecifier, indexStr));
        }

        return sbNodes.toString() + "\n" + sbIndexes.toString();
    }
}