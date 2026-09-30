import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Hidden tests for last year's Lab 9: Heaps and Heap Sort (school style: one test = one check). */
public class Tests {

    static final String F = "Lab09.java";
    static final String A = "ArrayHeap";
    static final String L = "LinkedHeap";

    static Object heap(String cls, Object... items) {
        Object h = T.make(cls);
        for (Object x : items) {
            T.call(h, "insert", x);
        }
        return h;
    }

    /** data[0..size-1] of an ArrayHeap. */
    static String arr(Object h) {
        int n = (Integer) T.call(h, "size");
        return Arrays.toString(Arrays.copyOf((Object[]) T.field(h, "data"), n));
    }

    /** Level-order values of a LinkedHeap, or a description of a broken parent link. */
    static String levels(Object h) {
        List<Object> out = new ArrayList<>();
        List<Object> q = new ArrayList<>();
        Object root = T.field(h, "root");
        if (root == null) {
            return "[]";
        }
        if (T.field(root, "parent") != null) {
            return "(root.parent must be null)";
        }
        q.add(root);
        while (!q.isEmpty() && out.size() < 500) {
            Object n = q.remove(0);
            out.add(T.field(n, "data"));
            for (String side : new String[] {"left", "right"}) {
                Object c = T.field(n, side);
                if (c != null) {
                    if (T.field(c, "parent") != n) {
                        return "(the parent reference of the node holding " + T.field(c, "data") + " is wrong)";
                    }
                    q.add(c);
                }
            }
        }
        return out.toString();
    }

    static String shape(Object n) {
        if (n == null) {
            return "-";
        }
        Object l = T.field(n, "left");
        Object r = T.field(n, "right");
        String d = String.valueOf(T.field(n, "data"));
        return l == null && r == null ? d : d + "(" + shape(l) + "," + shape(r) + ")";
    }

    static String drain(Object h) {
        StringBuilder sb = new StringBuilder();
        Object x;
        int k = 0;
        while ((x = T.call(h, "removeMin")) != null && k < 500) {
            sb.append(k++ > 0 ? " " : "").append(x);
        }
        return sb.toString();
    }

    static String sorted(Object[] a) {
        T.callStatic("Lab09", "heapSort", (Object) a);
        return Arrays.toString(a);
    }

    static void empty(String c) {
        Object h = T.make(c);
        T.expectNull("min() of an empty heap should be null", () -> T.call(h, "min"));
        T.expectNull("removeMin() of an empty heap should be null", () -> T.call(h, "removeMin"));
        T.expect("size() should stay 0", 0, () -> T.call(h, "size"));
        T.call(h, "insert", 7);
        T.expect("min() of a one-element heap should be 7", 7, () -> T.call(h, "min"));
        T.expect("removeMin() should return 7", 7, () -> T.call(h, "removeMin"));
        T.expectTrue("the heap must be empty again", () -> T.call(h, "isEmpty"));
        T.expectNull("removeMin() should now return null", () -> T.call(h, "removeMin"));
        T.call(h, "insert", (Object) null);
        T.expect("insert(null) must be ignored", 0, () -> T.call(h, "size"));
        T.call(h, "insert", 2);
        T.expect("the heap must work after being emptied: min() should be 2", 2, () -> T.call(h, "min"));
    }

    static void order(String c) {
        T.method(c, "removeMin", 0);
        Object h = heap(c, 9, 4, 7, 1, 8, 2, 2, 6);
        T.expect("min() should be 1", 1, () -> T.call(h, "min"));
        T.expect("size() should be 8", 8, () -> T.call(h, "size"));
        T.expect("removeMin until empty should give 1 2 2 4 6 7 8 9", "1 2 2 4 6 7 8 9", () -> drain(h));
        T.expect("strings should come out in order: ant bee cat dog", "ant bee cat dog",
                () -> drain(heap(c, "dog", "bee", "cat", "ant")));
    }

    static void random(String c) {
        java.util.Random rnd = new java.util.Random(9);
        java.util.PriorityQueue<Integer> ref = new java.util.PriorityQueue<>();
        Object h = T.make(c);
        Object twin = T.make(c.equals(A) ? L : A);
        boolean ok = true;
        String where = "";
        for (int step = 0; step < 300 && ok; step++) {
            if (ref.isEmpty() || rnd.nextInt(5) < 3) {
                int v = rnd.nextInt(40);
                ref.add(v);
                T.call(h, "insert", v);
                T.call(twin, "insert", v);
            } else {
                Object want = ref.poll();
                Object got = T.call(h, "removeMin");
                T.call(twin, "removeMin");
                if (!want.equals(got)) {
                    ok = false;
                    where = " (at step " + step + " removeMin returned " + got + " instead of " + want + ")";
                }
            }
            if (ok) {
                String a = c.equals(A) ? arr(h) : arr(twin);
                String l = c.equals(A) ? levels(twin) : levels(h);
                if (!a.equals(l)) {
                    ok = false;
                    where = " (after step " + step + " the array heap is " + a + " but the linked heap is " + l + ")";
                }
            }
        }
        T.expectTrue("300 random inserts/removeMins: always the minimum, and ArrayHeap and LinkedHeap in the same positions"
                + where, ok);
    }

    public static void main(String[] args) {

        T.test("ArrayHeap", "test_ArrayHeap_empty", "ArrayHeap empty and one element", () -> empty(A));

        T.test("ArrayHeap", "test_ArrayHeap_insert", "ArrayHeap insert and heap-up", () -> {
            T.method(A, "insert", 1);
            Object h = heap(A, 5);
            T.expect("insert 5: [5]", "[5]", () -> arr(h));
            T.call(h, "insert", 3);
            T.expect("insert 3 goes up: [3, 5]", "[3, 5]", () -> arr(h));
            T.call(h, "insert", 8);
            T.expect("insert 8 stays: [3, 5, 8]", "[3, 5, 8]", () -> arr(h));
            T.call(h, "insert", 1);
            T.expect("insert 1 goes up to the root: [1, 3, 8, 5]", "[1, 3, 8, 5]", () -> arr(h));
            T.call(h, "insert", 4);
            T.expect("insert 4: [1, 3, 8, 5, 4]", "[1, 3, 8, 5, 4]", () -> arr(h));
            T.call(h, "insert", 3);
            T.expect("insert 3 moves above its parent 8: [1, 3, 3, 5, 4, 8]", "[1, 3, 3, 5, 4, 8]",
                    () -> arr(h));
            Object e = heap(A, 2, 2);
            T.expect("an equal parent is not swapped: [2, 2]", "[2, 2]", () -> arr(e));
        });

        T.test("ArrayHeap", "test_ArrayHeap_removeMin", "ArrayHeap removeMin and heap-down", () -> {
            Object h = heap(A, 5, 3, 8, 1, 4);
            T.expect("removeMin() should return 1", 1, () -> T.call(h, "removeMin"));
            T.expect("after removeMin the array should be [3, 4, 8, 5]", "[3, 4, 8, 5]", () -> arr(h));
            T.expectNull("the old last slot must be cleared", () -> ((Object[]) T.field(h, "data"))[4]);
            T.expect("removeMin() should return 3", 3, () -> T.call(h, "removeMin"));
            T.expect("the array should be [4, 5, 8]", "[4, 5, 8]", () -> arr(h));
            Object t = heap(A, 1, 3, 3, 9, 9);
            T.call(t, "removeMin");
            T.expect("with equal children heap-down takes the left one: [3, 9, 3, 9]", "[3, 9, 3, 9]", () -> arr(t));
        });

        T.test("ArrayHeap", "test_ArrayHeap_order", "ArrayHeap returns elements in order", () -> order(A));

        T.test("ArrayHeap", "test_ArrayHeap_grow", "ArrayHeap holds more than 64 elements", () -> {
            Object h = T.make(A);
            for (int i = 100; i >= 1; i--) {
                T.call(h, "insert", i);
            }
            T.expect("after 100 inserts size() should be 100", 100, () -> T.call(h, "size"));
            T.expect("min() should be 1", 1, () -> T.call(h, "min"));
            T.expectTrue("removeMin 100 times should return 1, 2, ..., 100", () -> {
                for (int i = 1; i <= 100; i++) {
                    if (!Integer.valueOf(i).equals(T.call(h, "removeMin"))) {
                        return false;
                    }
                }
                return true;
            });
        });

        T.test("ArrayHeap", "test_ArrayHeap_merge", "ArrayHeap.merge", () -> {
            T.method(A, "merge", 2);
            Object h1 = heap(A, 5, 1, 9);
            Object h2 = heap(A, 4, 2);
            T.expect("h1 should be [1, 5, 9]", "[1, 5, 9]", () -> arr(h1));
            Object m = T.callStatic(A, "merge", h1, h2);
            T.expect("merge([1, 5, 9], [2, 4]) should be [1, 2, 9, 5, 4] (copy, then build-heap)", "[1, 2, 9, 5, 4]", () -> arr(m));
            T.expect("the merged heap should have size 5", 5, () -> T.call(m, "size"));
            T.expect("merge must not change h1", "[1, 5, 9]", () -> arr(h1));
            T.expect("merge must not change h2", "[2, 4]", () -> arr(h2));
            T.expectTrue("merge must return a new heap", () -> m != h1 && m != h2);
            Object m2 = T.callStatic(A, "merge", heap(A, 7, 8, 9), heap(A, 3, 1));
            T.expect("merge([7, 8, 9], [1, 3]) should be [1, 3, 9, 8, 7]", "[1, 3, 9, 8, 7]", () -> arr(m2));
            T.expect("the merged heap must work: removeMin order 1 3 7 8 9", "1 3 7 8 9", () -> drain(m2));
        });

        T.test("ArrayHeap", "test_ArrayHeap_mergeEdge", "ArrayHeap.merge with empty, null and big heaps", () -> {
            T.expect("merge([3, 4], empty) should be [3, 4]", "[3, 4]",
                    () -> arr(T.callStatic(A, "merge", heap(A, 3, 4), T.make(A))));
            T.expect("merge(null, [2]) should be [2]", "[2]", () -> arr(T.callStatic(A, "merge", null, heap(A, 2))));
            T.expect("merge(null, null) should be empty", "[]", () -> arr(T.callStatic(A, "merge", null, null)));
            Object big1 = T.make(A);
            Object big2 = T.make(A);
            for (int i = 0; i < 50; i++) {
                T.call(big1, "insert", 2 * i);
                T.call(big2, "insert", 2 * i + 1);
            }
            Object m = T.callStatic(A, "merge", big1, big2);
            T.expect("merging two heaps of 50 should give size 100", 100, () -> T.call(m, "size"));
            T.expectTrue("the merged heap of 0..99 should return 0, 1, ..., 99", () -> {
                for (int i = 0; i < 100; i++) {
                    if (!Integer.valueOf(i).equals(T.call(m, "removeMin"))) {
                        return false;
                    }
                }
                return true;
            });
        });

        T.test("ArrayHeap", "test_ArrayHeap_random", "ArrayHeap random operations", () -> random(A));

        T.test("LinkedHeap", "test_LinkedHeap_empty", "LinkedHeap empty and one element", () -> {
            empty(L);
            Object h = heap(L, 1);
            T.call(h, "removeMin");
            T.expectNull("root must be null after removing the only node", () -> T.field(h, "root"));
        });

        T.test("LinkedHeap", "test_LinkedHeap_insert", "LinkedHeap insert, shape and parents", () -> {
            T.method(L, "insert", 1);
            Object h = heap(L, 5, 3, 8, 1);
            T.expect("insert 5, 3, 8, 1 should give the shape 1(3(5,-),8)", "1(3(5,-),8)", () -> shape(T.field(h, "root")));
            T.expect("level order should be [1, 3, 8, 5] with correct parent references", "[1, 3, 8, 5]", () -> levels(h));
            T.call(h, "insert", 4);
            T.call(h, "insert", 0);
            T.expect("after inserting 4 and 0 the level order should be [0, 3, 1, 5, 4, 8]", "[0, 3, 1, 5, 4, 8]", () -> levels(h));
            T.expect("size() should be 6", 6, () -> T.call(h, "size"));
        });

        T.test("LinkedHeap", "test_LinkedHeap_removeMin", "LinkedHeap removeMin", () -> {
            Object h = heap(L, 5, 3, 8, 1, 4);
            T.expect("removeMin() should return 1", 1, () -> T.call(h, "removeMin"));
            T.expect("the level order should be [3, 4, 8, 5]", "[3, 4, 8, 5]", () -> levels(h));
            T.expect("removeMin() should return 3", 3, () -> T.call(h, "removeMin"));
            T.expect("the last node (a left child) is detached: shape 4(5,8)", "4(5,8)", () -> shape(T.field(h, "root")));
            T.expect("removeMin() should return 4", 4, () -> T.call(h, "removeMin"));
            T.expect("the last node (a right child) is detached: shape 5(8,-)", "5(8,-)", () -> shape(T.field(h, "root")));
            T.expect("the rest should come out as 5 8", "5 8", () -> drain(h));
        });

        T.test("LinkedHeap", "test_LinkedHeap_order", "LinkedHeap returns elements in order", () -> order(L));
        T.test("LinkedHeap", "test_LinkedHeap_random", "LinkedHeap random operations", () -> random(L));

        T.test("heapSort", "test_heapSort_basic", "heapSort", () -> {
            T.method("Lab09", "heapSort", 1);
            T.expect("heapSort([9, 4, 7, 1, 8]) should give [1, 4, 7, 8, 9]", "[1, 4, 7, 8, 9]",
                    () -> sorted(new Integer[] {9, 4, 7, 1, 8}));
            T.expect("heapSort with duplicates [3, 1, 3, 2, 1] should give [1, 1, 2, 3, 3]", "[1, 1, 2, 3, 3]",
                    () -> sorted(new Integer[] {3, 1, 3, 2, 1}));
            T.expect("heapSort of a sorted array should keep it sorted", "[1, 2, 3, 4, 5, 6]",
                    () -> sorted(new Integer[] {1, 2, 3, 4, 5, 6}));
            T.expect("heapSort of a reverse-sorted array", "[1, 2, 3, 4, 5, 6, 7]",
                    () -> sorted(new Integer[] {7, 6, 5, 4, 3, 2, 1}));
            T.expect("heapSort of strings", "[apple, fig, kiwi, pear]", () -> sorted(new String[] {"pear", "kiwi", "apple", "fig"}));
            T.expect("heapSort of negative numbers", "[-9, -3, 0, 4]", () -> sorted(new Integer[] {0, -3, 4, -9}));
        });

        T.test("heapSort", "test_heapSort_edge", "heapSort edge cases", () -> {
            T.expect("heapSort of an empty array should do nothing", "[]", () -> sorted(new Integer[0]));
            T.expect("heapSort of one element should do nothing", "[5]", () -> sorted(new Integer[] {5}));
            T.expect("heapSort of two elements [2, 1] should give [1, 2]", "[1, 2]", () -> sorted(new Integer[] {2, 1}));
            T.expectNoThrow("heapSort(null) must not throw", () -> T.callStatic("Lab09", "heapSort", (Object) null));
            java.util.Random rnd = new java.util.Random(3);
            Integer[] big = new Integer[500];
            for (int i = 0; i < big.length; i++) {
                big[i] = rnd.nextInt(100);
            }
            Integer[] want = big.clone();
            Arrays.sort(want);
            T.expect("heapSort of 500 random numbers should sort them", Arrays.toString(want), () -> sorted(big));
        });

        T.test("(code rules)", "test_Lab09_rules", "Lab09 code rules", () -> {
            T.expectTrue("ArrayHeap and LinkedHeap must implement Heap", () -> T.isA(A, "Heap") && T.isA(L, "Heap"));
            T.expectTrue("all fields must be private", () -> T.fieldsPrivate(A) && T.fieldsPrivate(L));
            T.expectFalse("do not use java.util.PriorityQueue or a library sort",
                    T.uses(F, "PriorityQueue") || T.matches(F, "\\.sort\\s*\\(") || T.uses(F, "TreeSet") || T.uses(F, "TreeMap"));
            T.expectFalse("no \"Not implemented\" stub may be left (no throw new ...)", T.matches(F, "\\bthrow\\s+new\\b"));
            T.expectSilent("no method except main may print", () -> {
                for (String c : new String[] {A, L}) {
                    try {
                        Object h = heap(c, 3, 1, 2);
                        T.call(h, "min");
                        drain(h);
                        T.call(h, "removeMin");
                    } catch (Throwable ignored) {
                        // printing is what matters here
                    }
                }
                try {
                    T.callStatic(A, "merge", heap(A, 1), heap(A, 2));
                    sorted(new Integer[] {2, 1});
                } catch (Throwable ignored) {
                    // printing is what matters here
                }
            });
        });

        T.done();
    }
}
