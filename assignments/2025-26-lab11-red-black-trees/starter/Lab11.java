import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * CS201 – Data Structures
 * Lab 11 – Red-Black Trees (last year's lab, 2025-2026)
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
        try {
            RBTree<Integer> tree = new RBTree<>();
            tree.insert(50);
            tree.insert(20);
            tree.insert(10);
            System.out.println(tree);   // [20|B]  [10|R]  [50|R]  [null|B] ...

            RBTree<Integer> t2 = new RBTree<>();
            int[] values = {33, 13, 53, 11, 21, 41, 61, 15, 31};
            for (int v : values) {
                t2.insert(v);
            }
            System.out.println(t2);
            System.out.println("size = " + t2.size() + ", contains(21) = " + t2.contains(21));
        } catch (UnsupportedOperationException e) {
            System.out.println();
            System.out.println("Stopped at a method that is not implemented yet:");
            System.out.println("  " + e.getStackTrace()[0]);
        }
        // You may test manually here if you want.
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
        // TODO: Return true if the tree is empty, false otherwise.
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public int size() {
        // TODO: Return the number of elements in the tree.
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Node<E> rotateLeft(Node<E> node) {
        // TODO: Implement Left Rotation.
        // 1. Identify the right child.
        // 2. Turn the right child's left subtree into node's right subtree.
        // 3. Update the parent pointer of that subtree (if it exists).
        // 4. Link the new root (right child) to the node's parent.
        //    (Be careful to check if node was the Root, or a left/right child).
        // 5. Put 'node' as the left child of the new root.
        // 6. Update parent pointers for both 'node' and the new root.
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Node<E> rotateRight(Node<E> node) {
        // TODO: Implement Right Rotation.
        // 1. Identify the left child.
        // 2. Turn the left child's right subtree into node's left subtree.
        // 3. Update parent pointers.
        // 4. Link the new root to the node's parent (Handle Root case).
        // 5. Put 'node' as the right child of the new root.
        // 6. Update parent pointers.
        throw new UnsupportedOperationException("Not implemented");
    }

    private boolean isRed(Node<E> node) {
        // TODO: Helper method.
        // Return true if node is not null AND node.color is true.
        // Return false otherwise (null nodes are Black).
        throw new UnsupportedOperationException("Not implemented");
    }

    private Node<E> getSibling(Node<E> node) {
        // TODO: Helper method.
        // If node is root, return null.
        // Return the other child of node's parent.
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void insert(E data) {
        // TODO: Public insert method.
        // 1. Handle empty tree case (Create root, increment size, force Black).
        // 2. If not empty, call insertRec(root, data).
        // 3. Ensure root is always Black at the end.
        throw new UnsupportedOperationException("Not implemented");
    }

    private void insertRec(Node<E> node, E data) {
        // TODO: Recursive insertion (Standard BST logic).
        // 1. Compare data with node.data.
        // 2. Recurse Left or Right.
        // 3. Base Case: If the spot is null, create a new Node, link it to parent.
        // 4. CRITICAL: After creating the node, trigger check(newNode) to fix RB properties.
        // Note: Do not use return values to link nodes (use parent pointers).
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void check(Node<E> newNode) {
        // TODO: The "Fixer" method.
        // 1. Check if we have a violation (Is me and my parent Red?).
        // 2. If valid, return.
        // 3. If invalid, get the Uncle.
        // 4. Case 1: Uncle is Red -> call recolor(), then recursively call check(grandparent).
        // 5. Case 2/3: Uncle is Black -> call balance().
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void recolor(Node<E> grandParent) {
        // TODO: Case 1 Implementation.
        // 1. Set Grandparent to Red (if not root).
        // 2. Set Parent to Black.
        // 3. Set Uncle to Black.
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Node<E> balance(Node<E> newNode) {
        // TODO: Case 2 & 3 Implementation (Rotations).
        // 1. Identify Parent and Grandparent.
        // 2. Determine if Parent is a Left or Right child.
        // 3. Determine if newNode is a Left or Right child (Inner vs Outer).
        // 4. Perform Rotations:
        //    - LL: rotateRight(grandParent)
        //    - RR: rotateLeft(grandParent)
        //    - LR: rotateLeft(parent) then rotateRight(grandParent)
        //    - RL: rotateRight(parent) then rotateLeft(grandParent)
        // 5. SWAP COLORS:
        //    - The new root of this subtree becomes Black.
        //    - The old Grandparent becomes Red.
        // 6. Return the new subtree root.
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean contains(E data) {
        return containsRec(root, data) != null;
    }

    private Node<E> containsRec(Node<E> node, E data) {
        // TODO: Standard BST Search
        throw new UnsupportedOperationException("Not implemented");
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