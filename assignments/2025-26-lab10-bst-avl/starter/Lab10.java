/**
 * CSE201 Lab 10 - Binary Search Trees and AVL Trees (last year's lab, 2025-2026).
 * Read the description before you start. Only Lab10 is public. No package declaration.
 */
public class Lab10 {
    public static void main(String[] args) {
        // You may test your BST and AVL implementations here.
        try {
            BST<Integer> bst = new BST<>();
            AVLTree<Integer> avl = new AVLTree<>();
            for (int i = 1; i <= 7; i++) {
                bst.insert(i);
                avl.insert(i);
            }
            System.out.println("BST contains(5)  = " + bst.contains(5));
            System.out.println("BST size()       = " + bst.size());
            System.out.println("AVL height()     = " + avl.height());      // 3 (a plain BST would have height 7)
            avl.remove(4);
            System.out.println("AVL contains(4)  = " + avl.contains(4));   // false
        } catch (UnsupportedOperationException e) {
            System.out.println();
            System.out.println("Stopped at a method that is not implemented yet:");
            System.out.println("  " + e.getStackTrace()[0]);
        }
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

// ---------------------------------------------
// IBST
// ---------------------------------------------
interface IBST<E extends Comparable<E>> {
    void insert(E element);
    void remove(E element);
    boolean contains(E element);
    int size();
    boolean isEmpty();

    Node<E> findMin(Node<E> n);
}

// ---------------------------------------------
// IAVL
// ---------------------------------------------
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
        // TODO: implement recursive search
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void insert(E element) {
        root = insertRec(root, element);
    }

    protected Node<E> insertRec(Node<E> n, E e) {
        // TODO: standard BST insert (no balancing)
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void remove(E element) {
        root = removeRec(root, element);
    }

    protected Node<E> removeRec(Node<E> n, E e) {
        // TODO: BST remove (3 cases)
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Node<E> findMin(Node<E> n) {
        // TODO: return leftmost node
        throw new UnsupportedOperationException("Not implemented");
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
        // TODO:
        // 1) perform BST insert
        // 2) update height
        // 3) return balance(n)
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    protected Node<E> removeRec(Node<E> n, E e) {
        // TODO:
        // 1) perform BST remove
        // 2) update height
        // 3) return balance(n)
        throw new UnsupportedOperationException("Not implemented");
    }

    // -------------------------------------
    // AVL Helpers
    // -------------------------------------

    private void updateHeight(Node<E> n) {
        // TODO: recompute this node's height based on its children
        throw new UnsupportedOperationException("Not implemented");
    }


    @Override
    public int height() {
        // TODO: return height(root)
        throw new UnsupportedOperationException("Not implemented");
    }

    private int height(Node<E> n) {
        // TODO: null -> 0, else n.height
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public int getBalanceFactor(Node<E> n) {
        // TODO: compare heights of left and right subtrees
        throw new UnsupportedOperationException("Not implemented");
    }


    @Override
    public Node<E> balance(Node<E> n) {
        // TODO:
        // 1) compute how unbalanced this node is (balance factor)
        //
        // 2) If the left subtree is taller than the right:
        //      - look at the LEFT child:
        //        a) if the left child also leans to its LEFT,
        //           perform a single RIGHT rotation
        //              (the left child becomes the new root of this subtree)
        //        b) if the left child leans to its RIGHT,
        //           first rotate the left child LEFT,
        //           then rotate this node RIGHT
        //
        // 3) If the right subtree is taller than the left:
        //      - look at the RIGHT child:
        //        a) if the right child also leans to its RIGHT,
        //           perform a single LEFT rotation
        //              (the right child becomes the new root of this subtree)
        //        b) if the right child leans to its LEFT,
        //           first rotate the right child RIGHT,
        //           then rotate this node LEFT
        //
        // 4) return the new root of this subtree after rotations
        throw new UnsupportedOperationException("Not implemented");
    }

    // -------------------------------------
    // Rotations
    // -------------------------------------

    @Override
    public Node<E> rotateLeft(Node<E> y) {
        // TODO:
        // Left rotation:
        // The right child becomes the new root of this subtree,
        // the original root becomes the left child of that node,
        // and the left subtree of the new root must be reattached
        // as the right subtree of the old root.
        //
        // Visually:
        //        y                     x
        //         \                   / \
        //          x      --->       y   R
        //         / \                 \
        //        L   R                L
        //
        // After rearranging, update heights of affected nodes
        // and return the new subtree root.
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Node<E> rotateRight(Node<E> x) {
        // TODO:
        // Right rotation:
        // The left child becomes the new root of this subtree,
        // the original root becomes the right child of that node,
        // and the right subtree of the new root must be reattached
        // as the left subtree of the old root.
        //
        // Visually:
        //        x                     y
        //       /                     / \
        //      y         --->        L   x
        //     / \                       /
        //    L   R                     R
        //
        // After rearranging, update heights of affected nodes
        // and return the new subtree root.
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Node<E> rotateLeftRight(Node<E> n) {
        // TODO: LR = rotateLeft(left) then rotateRight
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Node<E> rotateRightLeft(Node<E> n) {
        // TODO: RL = rotateRight(right) then rotateLeft
        throw new UnsupportedOperationException("Not implemented");
    }
}
