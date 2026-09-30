/**
 * CSE201 Lab 5 - Recursion (last year's lab, 2025-2026).
 *
 * Read the description before you start. Everything you write goes into this file.
 * The declarations below are the ones the description requires; fill in the bodies.
 *
 * Implement each function RECURSIVELY: no for, while or do-while loops anywhere in this file.
 * Only Lab05 is public. Do not add a package declaration.
 */
public class Lab05 {

    public static void main(String[] args) {
        try {
            System.out.println("factorial(5)                  = " + factorial(5));
            System.out.println("sum([4, 1, 3, 9], 3)          = " + sum(new int[] {4, 1, 3, 9}, 3));
            System.out.println("power(2, 10)                  = " + power(2, 10));
            System.out.println("binarySearch([1,3,5,7,9], 7)  = " + binarySearch(new int[] {1, 3, 5, 7, 9}, 7, 0, 4));
            System.out.println("reverse(\"hello\")              = " + reverse("hello"));
            System.out.println("fib(10)                       = " + fib(10));
            System.out.println("tailSum(4, 0)                 = " + tailSum(4, 0));

            Node head = buildList(1, 2, 3, 4);
            System.out.println("listSum(1 -> 2 -> 3 -> 4)     = " + listSum(head));
            System.out.println("contains(list, 3)             = " + contains(head, 3));
            Node rev = reverseList(head);
            System.out.println("reverseList(...).data         = " + (rev == null ? "null" : rev.data));
        } catch (UnsupportedOperationException e) {
            System.out.println();
            System.out.println("Stopped at a method that is not implemented yet:");
            System.out.println("  " + e.getStackTrace()[0]);
        }
    }

    // Implement each function below recursively.

    public static long factorial(int n) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public static int sum(int[] arr, int n) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public static long power(long base, int exp) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public static int binarySearch(int[] arr, int target, int low, int high) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public static String reverse(String s) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public static long fib(int n) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public static long tailSum(int n, long acc) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public static int listSum(Node head) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public static boolean contains(Node head, int target) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public static Node reverseList(Node head) {
        throw new UnsupportedOperationException("Not implemented");
    }

    // ---------- Given helper: do not change ----------

    /** Builds a linked list with the given values, in order. buildList() returns null. */
    public static Node buildList(int... vals) {
        return buildFrom(vals, 0);
    }

    private static Node buildFrom(int[] vals, int i) {
        if (i == vals.length) {
            return null;
        }
        Node node = new Node(vals[i]);
        node.next = buildFrom(vals, i + 1);
        return node;
    }
}

/** Given: a node of a singly linked list of ints. Do not change. */
class Node {
    int data;
    Node next;

    Node(int data) {
        this.data = data;
        this.next = null;
    }
}
