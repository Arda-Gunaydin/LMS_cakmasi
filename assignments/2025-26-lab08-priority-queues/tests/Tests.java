import java.math.BigDecimal;
import java.util.Arrays;

/** Hidden tests for last year's Lab 8: Priority Queues (school style: one test = one check). */
public class Tests {

    static final String F = "Lab08.java";
    static final String U = "UnsortedArrayPQ";
    static final String S = "SortedLinkedPQ";

    static Object pq(String cls, Object... items) {
        Object q = T.make(cls);
        for (Object x : items) {
            T.call(q, "insert", x);
        }
        return q;
    }

    /** The first n slots of the array as text. */
    static String slots(Object q, int n) {
        return Arrays.toString(Arrays.copyOf((Object[]) T.field(q, "data"), n));
    }

    /** The linked nodes from head as text: 1 -> 2 -> 5. */
    static String chain(Object q) {
        StringBuilder sb = new StringBuilder();
        Object n = T.field(q, "head");
        int k = 0;
        while (n != null && k < 50) {
            sb.append(k++ > 0 ? " -> " : "").append(T.field(n, "data"));
            n = T.field(n, "next");
        }
        return sb.toString();
    }

    /** removeMin until empty. */
    static String drain(Object q) {
        StringBuilder sb = new StringBuilder();
        Object x;
        int k = 0;
        while ((x = T.call(q, "removeMin")) != null && k < 100) {
            sb.append(k++ > 0 ? " " : "").append(x);
        }
        return sb.toString();
    }

    static void empty(String c) {
        Object q = T.make(c);
        T.expectTrue("a new " + c + " must be empty", () -> T.call(q, "isEmpty"));
        T.expect("size() should be 0", 0, () -> T.call(q, "size"));
        T.expectNull("min() on an empty queue should return null", () -> T.call(q, "min"));
        T.expectNull("removeMin() on an empty queue should return null", () -> T.call(q, "removeMin"));
        T.expect("size() should still be 0", 0, () -> T.call(q, "size"));
        T.call(q, "insert", 4);
        T.call(q, "removeMin");
        T.expectNull("after removing the only element, min() should be null", () -> T.call(q, "min"));
        T.expectTrue("the queue must be empty again", () -> T.call(q, "isEmpty"));
        T.call(q, "insert", (Object) null);
        T.expect("insert(null) must be ignored", 0, () -> T.call(q, "size"));
    }

    static void order(String c) {
        T.method(c, "removeMin", 0);
        Object q = pq(c, 5, 2, 9, 1, 7);
        T.expect("min() of {5, 2, 9, 1, 7} should be 1", 1, () -> T.call(q, "min"));
        T.expect("min() must not remove: size() should be 5", 5, () -> T.call(q, "size"));
        T.expect("removeMin() should return 1", 1, () -> T.call(q, "removeMin"));
        T.expect("min() should then be 2", 2, () -> T.call(q, "min"));
        T.call(q, "insert", 0);
        T.call(q, "insert", 8);
        T.expect("after inserting 0 and 8, the elements should come out as 0 2 5 7 8 9", "0 2 5 7 8 9", () -> drain(q));
        T.expect("duplicates: {3, 1, 3, 1} should come out as 1 1 3 3", "1 1 3 3", () -> drain(pq(c, 3, 1, 3, 1)));
        T.expect("strings: {pear, apple, fig} should come out as apple fig pear", "apple fig pear",
                () -> drain(pq(c, "pear", "apple", "fig")));
        T.expect("negative numbers: {-1, -7, 4} should come out as -7 -1 4", "-7 -1 4", () -> drain(pq(c, -1, -7, 4)));
    }

    static void random(String c) {
        java.util.Random rnd = new java.util.Random(8);
        java.util.PriorityQueue<Integer> ref = new java.util.PriorityQueue<>();
        Object q = T.make(c);
        boolean ok = true;
        String where = "";
        for (int step = 0; step < 200 && ok; step++) {
            if (ref.size() < 16 && (ref.isEmpty() || rnd.nextInt(3) > 0)) {
                int v = rnd.nextInt(50) - 10;
                ref.add(v);
                T.call(q, "insert", v);
            } else {
                Object want = ref.poll();
                Object got = T.call(q, "removeMin");
                if (!want.equals(got)) {
                    ok = false;
                    where = " (at step " + step + " removeMin returned " + got + " instead of " + want + ")";
                }
            }
            if (ok && !Integer.valueOf(ref.size()).equals(T.call(q, "size"))) {
                ok = false;
                where = " (size() is wrong at step " + step + ")";
            }
        }
        T.expectTrue("200 random inserts and removeMins must always return the smallest element" + where, ok);
    }

    public static void main(String[] args) {

        T.test("UnsortedArrayPQ", "test_UnsortedArrayPQ_empty", "UnsortedArrayPQ when empty", () -> empty(U));
        T.test("UnsortedArrayPQ", "test_UnsortedArrayPQ_order", "UnsortedArrayPQ min and removeMin", () -> order(U));

        T.test("UnsortedArrayPQ", "test_UnsortedArrayPQ_layout", "UnsortedArrayPQ array layout", () -> {
            T.method(U, "insert", 1);
            Object q = pq(U, 5, 2, 9, 1, 7);
            T.expect("insert must append without sorting: data should start [5, 2, 9, 1, 7, null]", "[5, 2, 9, 1, 7, null]",
                    () -> slots(q, 6));
            T.call(q, "min");
            T.expect("min() must not change the array", "[5, 2, 9, 1, 7, null]", () -> slots(q, 6));
            T.call(q, "removeMin");
            T.expect("removeMin() moves the last element into the min slot: [5, 2, 9, 7, null, null]", "[5, 2, 9, 7, null, null]",
                    () -> slots(q, 6));
            T.call(q, "removeMin");
            T.expect("the next removeMin() gives [5, 7, 9, null, null, null]", "[5, 7, 9, null, null, null]", () -> slots(q, 6));
            Object r = pq(U, 1, 4, 6);
            T.call(r, "removeMin");
            T.expect("removing the min at index 0 of [1, 4, 6] gives [6, 4, null]", "[6, 4, null]", () -> slots(r, 3));
            Object t = pq(U, 4, 6, 1);
            T.call(t, "removeMin");
            T.expect("removing the min at the last index of [4, 6, 1] gives [4, 6, null]", "[4, 6, null]", () -> slots(t, 3));
        });

        T.test("UnsortedArrayPQ", "test_UnsortedArrayPQ_ties", "UnsortedArrayPQ equal elements", () -> {
            BigDecimal a = new BigDecimal("1.0");
            BigDecimal b = new BigDecimal("1.00");
            Object q = pq(U, new BigDecimal("3"), a, b);
            T.expectSame("with two equally small elements, min() should be the one at the lower index (1.0)", a,
                    () -> T.call(q, "min"));
            T.expectSame("removeMin() should first return 1.0 (lower index)", a, () -> T.call(q, "removeMin"));
            T.expectSame("then 1.00", b, () -> T.call(q, "removeMin"));
        });

        T.test("UnsortedArrayPQ", "test_UnsortedArrayPQ_full", "UnsortedArrayPQ is full at 16", () -> {
            Object q = T.make(U);
            for (int i = 16; i >= 1; i--) {
                T.call(q, "insert", i);
            }
            T.expect("size() should be 16", 16, () -> T.call(q, "size"));
            String out = T.captureOut(() -> T.call(q, "insert", 0));
            T.expect("insert into a full queue should print PQ is full!", "PQ is full!", () -> out.trim());
            T.expect("the element must not be inserted: size() stays 16", 16, () -> T.call(q, "size"));
            T.expect("min() should still be 1", 1, () -> T.call(q, "min"));
            T.expect("the array must not be resized", 16, () -> ((Object[]) T.field(q, "data")).length);
            T.call(q, "removeMin");
            T.expect("after one removeMin there is room again", "", () -> T.captureOut(() -> T.call(q, "insert", 0)).trim());
            T.expect("min() should now be 0", 0, () -> T.call(q, "min"));
        });

        T.test("UnsortedArrayPQ", "test_UnsortedArrayPQ_random", "UnsortedArrayPQ random operations", () -> random(U));

        T.test("SortedLinkedPQ", "test_SortedLinkedPQ_empty", "SortedLinkedPQ when empty", () -> {
            empty(S);
            Object q = pq(S, 1);
            T.call(q, "removeMin");
            T.expectNull("head must be null after removing the only element", () -> T.field(q, "head"));
        });
        T.test("SortedLinkedPQ", "test_SortedLinkedPQ_order", "SortedLinkedPQ min and removeMin", () -> order(S));

        T.test("SortedLinkedPQ", "test_SortedLinkedPQ_sorted", "SortedLinkedPQ keeps its nodes sorted", () -> {
            T.method(S, "insert", 1);
            Object q = pq(S, 5);
            T.expect("insert into an empty list: 5", "5", () -> chain(q));
            T.call(q, "insert", 2);
            T.expect("insert before head: 2 -> 5", "2 -> 5", () -> chain(q));
            T.call(q, "insert", 9);
            T.expect("insert at the end: 2 -> 5 -> 9", "2 -> 5 -> 9", () -> chain(q));
            T.call(q, "insert", 7);
            T.expect("insert in the middle: 2 -> 5 -> 7 -> 9", "2 -> 5 -> 7 -> 9", () -> chain(q));
            T.call(q, "insert", 1);
            T.expect("insert a new smallest: 1 -> 2 -> 5 -> 7 -> 9", "1 -> 2 -> 5 -> 7 -> 9", () -> chain(q));
            T.call(q, "insert", 5);
            T.expect("insert a duplicate: 1 -> 2 -> 5 -> 5 -> 7 -> 9", "1 -> 2 -> 5 -> 5 -> 7 -> 9", () -> chain(q));
            T.expect("size() should be 6", 6, () -> T.call(q, "size"));
            T.call(q, "removeMin");
            T.expect("removeMin removes the head: 2 -> 5 -> 5 -> 7 -> 9", "2 -> 5 -> 5 -> 7 -> 9", () -> chain(q));
        });

        T.test("SortedLinkedPQ", "test_SortedLinkedPQ_ties", "SortedLinkedPQ equal elements are first in, first out", () -> {
            BigDecimal a = new BigDecimal("1.0");
            BigDecimal b = new BigDecimal("1.00");
            BigDecimal c = new BigDecimal("1.000");
            Object q = pq(S, a, new BigDecimal("2"), b, new BigDecimal("0.5"), c);
            T.expect("the order should be 0.5 -> 1.0 -> 1.00 -> 1.000 -> 2", "0.5 -> 1.0 -> 1.00 -> 1.000 -> 2", () -> chain(q));
            T.call(q, "removeMin");
            T.expectSame("among equal elements the first inserted (1.0) should come out first", a, () -> T.call(q, "removeMin"));
            T.expectSame("then 1.00", b, () -> T.call(q, "removeMin"));
            T.expectSame("then 1.000", c, () -> T.call(q, "removeMin"));
        });

        T.test("SortedLinkedPQ", "test_SortedLinkedPQ_random", "SortedLinkedPQ random operations", () -> {
            random(S);
            Object q = T.make(S);
            for (int i = 0; i < 40; i++) {
                T.call(q, "insert", (i * 17) % 40);
            }
            T.expect("SortedLinkedPQ has no capacity limit: 40 inserts give size 40", 40, () -> T.call(q, "size"));
        });

        T.test("(code rules)", "test_Lab08_rules", "Lab08 code rules", () -> {
            T.expectTrue("both classes must implement PriorityQueue", () -> T.isA(U, "PriorityQueue") && T.isA(S, "PriorityQueue"));
            T.expectTrue("all fields must be private", () -> T.fieldsPrivate(U) && T.fieldsPrivate(S));
            T.expectFalse("Lab08.java must not use java.util", T.matches(F, "\\bjava\\s*\\.\\s*util\\b"));
            T.expectFalse("no method may throw an exception (remove every \"Not implemented\" stub)", T.matches(F, "\\bthrow\\s+new\\b"));
            T.expectSilent("nothing may be printed except PQ is full!", () -> {
                for (String c : new String[] {U, S}) {
                    try {
                        Object q = pq(c, 3, 1, 2);
                        T.call(q, "min");
                        drain(q);
                        T.call(q, "min");
                        T.call(q, "removeMin");
                    } catch (Throwable ignored) {
                        // printing is what matters here
                    }
                }
            });
        });

        T.done();
    }
}
