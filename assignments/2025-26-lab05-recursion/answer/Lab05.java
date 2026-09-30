public class Lab05 {

    public static void main(String[] args) {
    }

    // Implement each function below recursively.

    public static long factorial(int n) {
        if (n < 0) throw new IllegalArgumentException();
        if (n <= 1) return 1L;
        return n * factorial(n - 1);
    }

    public static int sum(int[] arr, int n) {
        if (arr == null || n < 0 || n > arr.length) throw new IllegalArgumentException();
        if (n == 0) return 0;
        return sum(arr, n - 1) + arr[n - 1];
    }

    public static long power(long base, int exp) {
        if (exp < 0) throw new IllegalArgumentException();
        if (exp == 0) return 1L;
        long half = power(base, exp / 2);
        return exp % 2 == 0 ? half * half : half * half * base;
    }

    public static int binarySearch(int[] arr, int target, int low, int high) {
        if (arr == null) throw new IllegalArgumentException();
        if (low > high) return -1;
        int mid = low + (high - low) / 2;
        if (arr[mid] == target) return mid;
        if (arr[mid] < target) return binarySearch(arr, target, mid + 1, high);
        return binarySearch(arr, target, low, mid - 1);
    }

    public static String reverse(String s) {
        if (s == null) throw new IllegalArgumentException();
        if (s.length() <= 1) return s;
        return reverse(s.substring(1)) + s.charAt(0);
    }

    public static long fib(int n) {
        if (n < 0) throw new IllegalArgumentException();
        if (n <= 1) return n;
        return fib(n - 1) + fib(n - 2);
    }

    public static long tailSum(int n, long acc) {
        if (n <= 0) return acc;
        return tailSum(n - 1, acc + n);
    }

    public static int listSum(Node head) {
        if (head == null) return 0;
        return head.data + listSum(head.next);
    }

    public static boolean contains(Node head, int target) {
        if (head == null) return false;
        return head.data == target || contains(head.next, target);
    }

    public static Node reverseList(Node head) {
        if (head == null || head.next == null) return head;
        Node rest = reverseList(head.next);
        head.next.next = head;
        head.next = null;
        return rest;
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
