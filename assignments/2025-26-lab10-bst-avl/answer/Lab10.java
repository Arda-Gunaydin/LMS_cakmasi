public class Lab10 {
    public static void main(String[] args) {
    }
}

// ---------------------------------------------
// Node
// ---------------------------------------------
class Node<E extends Comparable<E>> {
    E data;
    Node<E> left;
    Node<E> right;
    int height;

    Node(E data) {
        this.data = data;
        this.height = 1; // for AVL
    }
}

interface IBST<E extends Comparable<E>> {
    void insert(E element);
    void remove(E element);
    boolean contains(E element);
    int size();
    boolean isEmpty();

    Node<E> findMin(Node<E> n);
}

interface IAVL<E extends Comparable<E>> extends IBST<E> {
    int height();
    int getBalanceFactor(Node<E> node);

    Node<E> balance(Node<E> node);

    Node<E> rotateLeft(Node<E> node);
    Node<E> rotateRight(Node<E> node);
    Node<E> rotateLeftRight(Node<E> node);
    Node<E> rotateRightLeft(Node<E> node);
}

// ---------------------------------------------
// BST Implementation (Unbalanced)
// ---------------------------------------------
class BST<E extends Comparable<E>> implements IBST<E> {

    protected Node<E> root;
    protected int size;

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(E element) {
        return containsRec(root, element);
    }

    protected boolean containsRec(Node<E> n, E e) {
        if (n == null || e == null) return false;
        int c = e.compareTo(n.data);
        if (c == 0) return true;
        return c < 0 ? containsRec(n.left, e) : containsRec(n.right, e);
    }

    @Override
    public void insert(E element) {
        root = insertRec(root, element);
    }

    protected Node<E> insertRec(Node<E> n, E e) {
        if (e == null) return n;
        if (n == null) {
            size++;
            return new Node<>(e);
        }
        int c = e.compareTo(n.data);
        if (c < 0) n.left = insertRec(n.left, e);
        else if (c > 0) n.right = insertRec(n.right, e);
        return n;   // duplicate: nothing changes
    }

    @Override
    public void remove(E element) {
        root = removeRec(root, element);
    }

    protected Node<E> removeRec(Node<E> n, E e) {
        if (n == null || e == null) return n;
        int c = e.compareTo(n.data);
        if (c < 0) {
            n.left = removeRec(n.left, e);
        } else if (c > 0) {
            n.right = removeRec(n.right, e);
        } else if (n.left == null) {
            size--;
            return n.right;
        } else if (n.right == null) {
            size--;
            return n.left;
        } else {
            Node<E> s = findMin(n.right);   // in-order successor
            n.data = s.data;
            n.right = removeRec(n.right, s.data);
        }
        return n;
    }

    @Override
    public Node<E> findMin(Node<E> n) {
        if (n == null) return null;
        while (n.left != null) n = n.left;
        return n;
    }
}

// ---------------------------------------------
// AVL Implementation (Balanced BST)
// ---------------------------------------------
class AVLTree<E extends Comparable<E>>
        extends BST<E>
        implements IAVL<E> {

    @Override
    protected Node<E> insertRec(Node<E> n, E e) {
        if (e == null) return n;
        if (n == null) {
            size++;
            return new Node<>(e);
        }
        int c = e.compareTo(n.data);
        if (c < 0) n.left = insertRec(n.left, e);
        else if (c > 0) n.right = insertRec(n.right, e);
        else return n;
        updateHeight(n);
        return balance(n);
    }

    @Override
    protected Node<E> removeRec(Node<E> n, E e) {
        if (n == null || e == null) return n;
        int c = e.compareTo(n.data);
        if (c < 0) {
            n.left = removeRec(n.left, e);
        } else if (c > 0) {
            n.right = removeRec(n.right, e);
        } else if (n.left == null) {
            size--;
            return n.right;
        } else if (n.right == null) {
            size--;
            return n.left;
        } else {
            Node<E> s = findMin(n.right);
            n.data = s.data;
            n.right = removeRec(n.right, s.data);
        }
        updateHeight(n);
        return balance(n);
    }

    // -------------------------------------
    // AVL Helpers
    // -------------------------------------

    private void updateHeight(Node<E> n) {
        n.height = 1 + Math.max(height(n.left), height(n.right));
    }

    @Override
    public int height() {
        return height(root);
    }

    private int height(Node<E> n) {
        return n == null ? 0 : n.height;
    }

    @Override
    public int getBalanceFactor(Node<E> n) {
        return n == null ? 0 : height(n.left) - height(n.right);
    }

    @Override
    public Node<E> balance(Node<E> n) {
        if (n == null) return null;
        int bf = getBalanceFactor(n);
        if (bf > 1) {
            return getBalanceFactor(n.left) >= 0 ? rotateRight(n) : rotateLeftRight(n);
        }
        if (bf < -1) {
            return getBalanceFactor(n.right) <= 0 ? rotateLeft(n) : rotateRightLeft(n);
        }
        return n;
    }

    // -------------------------------------
    // Rotations
    // -------------------------------------

    @Override
    public Node<E> rotateLeft(Node<E> y) {
        Node<E> x = y.right;
        y.right = x.left;
        x.left = y;
        updateHeight(y);
        updateHeight(x);
        return x;
    }

    @Override
    public Node<E> rotateRight(Node<E> x) {
        Node<E> y = x.left;
        x.left = y.right;
        y.right = x;
        updateHeight(x);
        updateHeight(y);
        return y;
    }

    @Override
    public Node<E> rotateLeftRight(Node<E> n) {
        n.left = rotateLeft(n.left);
        return rotateRight(n);
    }

    @Override
    public Node<E> rotateRightLeft(Node<E> n) {
        n.right = rotateRight(n.right);
        return rotateLeft(n);
    }
}
