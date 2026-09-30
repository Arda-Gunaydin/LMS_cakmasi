/** Hidden tests for last year's Lab 5: Recursion (school style: one test = one check). */
public class Tests {

    static final String F = "Lab05.java";
    static final String C = "Lab05";
    static final Class<IllegalArgumentException> IAE = IllegalArgumentException.class;

    static Object build(int... vals) {
        return T.callStatic(C, "buildList", (Object) vals);
    }

    /** The list as text, e.g. "1 -> 2 -> 3"; stops after 20 nodes so a cycle cannot hang. */
    static String str(Object node) {
        StringBuilder sb = new StringBuilder();
        int n = 0;
        while (node != null && n < 20) {
            if (n > 0) {
                sb.append(" -> ");
            }
            sb.append(T.field(node, "data"));
            node = T.field(node, "next");
            n++;
        }
        return node == null ? sb.toString() : sb + " -> ... (cycle)";
    }

    public static void main(String[] args) {

        T.test("factorial", "test_factorial_values", "factorial", () -> {
            T.method(C, "factorial", 1);
            T.expect("factorial(0) should be 1", 1L, () -> T.callStatic(C, "factorial", 0));
            T.expect("factorial(1) should be 1", 1L, () -> T.callStatic(C, "factorial", 1));
            T.expect("factorial(5) should be 120", 120L, () -> T.callStatic(C, "factorial", 5));
            T.expect("factorial(20) should be 2432902008176640000", 2432902008176640000L,
                    () -> T.callStatic(C, "factorial", 20));
            T.expectThrows("factorial(-1) should throw IllegalArgumentException", IAE, () -> T.callStatic(C, "factorial", -1));
        });

        T.test("sum", "test_sum_values", "sum", () -> {
            T.method(C, "sum", 2);
            int[] a = {4, 1, 3, 9};
            T.expect("sum([4, 1, 3, 9], 4) should be 17", 17, () -> T.callStatic(C, "sum", a, 4));
            T.expect("sum([4, 1, 3, 9], 3) should be 8", 8, () -> T.callStatic(C, "sum", a, 3));
            T.expect("sum([4, 1, 3, 9], 1) should be 4", 4, () -> T.callStatic(C, "sum", a, 1));
            T.expect("sum([4, 1, 3, 9], 0) should be 0", 0, () -> T.callStatic(C, "sum", a, 0));
            T.expect("sum([-5, 5, -2], 3) should be -2", -2, () -> T.callStatic(C, "sum", new int[] {-5, 5, -2}, 3));
            T.expect("sum([], 0) should be 0", 0, () -> T.callStatic(C, "sum", new int[0], 0));
            T.expect("sum must not change the array", "[4, 1, 3, 9]", () -> java.util.Arrays.toString(a));
        });

        T.test("sum", "test_sum_invalid", "sum with invalid input", () -> {
            T.expectThrows("sum([1, 2], 3) should throw IllegalArgumentException", IAE,
                    () -> T.callStatic(C, "sum", new int[] {1, 2}, 3));
            T.expectThrows("sum([1, 2], -1) should throw IllegalArgumentException", IAE,
                    () -> T.callStatic(C, "sum", new int[] {1, 2}, -1));
            T.expectThrows("sum(null, 0) should throw IllegalArgumentException", IAE,
                    () -> T.callStatic(C, "sum", null, 0));
        });

        T.test("power", "test_power_values", "power", () -> {
            T.method(C, "power", 2);
            T.expect("power(2, 10) should be 1024", 1024L, () -> T.callStatic(C, "power", 2L, 10));
            T.expect("power(3, 5) should be 243", 243L, () -> T.callStatic(C, "power", 3L, 5));
            T.expect("power(-3, 3) should be -27", -27L, () -> T.callStatic(C, "power", -3L, 3));
            T.expect("power(5, 1) should be 5", 5L, () -> T.callStatic(C, "power", 5L, 1));
            T.expect("power(7, 0) should be 1", 1L, () -> T.callStatic(C, "power", 7L, 0));
            T.expect("power(0, 0) should be 1", 1L, () -> T.callStatic(C, "power", 0L, 0));
            T.expect("power(2, 62) should be 4611686018427387904", 4611686018427387904L, () -> T.callStatic(C, "power", 2L, 62));
            T.expectThrows("power(2, -1) should throw IllegalArgumentException", IAE, () -> T.callStatic(C, "power", 2L, -1));
        });

        T.test("power", "test_power_fast", "power must be fast (divide and conquer)", () -> {
            T.expect("power(1, 1000000000) should be 1 and finish quickly (about 30 recursive calls)", 1L,
                    () -> T.callStatic(C, "power", 1L, 1000000000));
            T.expect("power(-1, 999999999) should be -1 and finish quickly", -1L,
                    () -> T.callStatic(C, "power", -1L, 999999999));
        });

        T.test("binarySearch", "test_binarySearch_found", "binarySearch finds values", () -> {
            T.method(C, "binarySearch", 4);
            int[] a = {1, 3, 5, 7, 9, 11, 13};
            T.expect("binarySearch([1..13 odd], 7, 0, 6) should be 3", 3, () -> T.callStatic(C, "binarySearch", a, 7, 0, 6));
            T.expect("binarySearch(..., 1, 0, 6) should be 0 (first element)", 0, () -> T.callStatic(C, "binarySearch", a, 1, 0, 6));
            T.expect("binarySearch(..., 13, 0, 6) should be 6 (last element)", 6, () -> T.callStatic(C, "binarySearch", a, 13, 0, 6));
            T.expect("binarySearch(..., 11, 0, 6) should be 5", 5, () -> T.callStatic(C, "binarySearch", a, 11, 0, 6));
            T.expect("binarySearch([42], 42, 0, 0) should be 0", 0, () -> T.callStatic(C, "binarySearch", new int[] {42}, 42, 0, 0));
            T.expect("binarySearch([-9, -4, 0, 6], -4, 0, 3) should be 1", 1,
                    () -> T.callStatic(C, "binarySearch", new int[] {-9, -4, 0, 6}, -4, 0, 3));
        });

        T.test("binarySearch", "test_binarySearch_missing", "binarySearch missing values and ranges", () -> {
            int[] a = {1, 3, 5, 7, 9};
            T.expect("binarySearch([1, 3, 5, 7, 9], 4, 0, 4) should be -1", -1, () -> T.callStatic(C, "binarySearch", a, 4, 0, 4));
            T.expect("binarySearch(..., 0, 0, 4) should be -1 (smaller than all)", -1, () -> T.callStatic(C, "binarySearch", a, 0, 0, 4));
            T.expect("binarySearch(..., 10, 0, 4) should be -1 (larger than all)", -1, () -> T.callStatic(C, "binarySearch", a, 10, 0, 4));
            T.expect("binarySearch(..., 9, 0, 2) should be -1 (9 is outside the range)", -1,
                    () -> T.callStatic(C, "binarySearch", a, 9, 0, 2));
            T.expect("binarySearch(..., 3, 2, 4) should be -1 (3 is outside the range)", -1,
                    () -> T.callStatic(C, "binarySearch", a, 3, 2, 4));
            T.expect("binarySearch(..., 7, 2, 4) should be 3", 3, () -> T.callStatic(C, "binarySearch", a, 7, 2, 4));
            T.expect("binarySearch([], 1, 0, -1) should be -1 (empty range)", -1,
                    () -> T.callStatic(C, "binarySearch", new int[0], 1, 0, -1));
            T.expectThrows("binarySearch(null, 1, 0, 0) should throw IllegalArgumentException", IAE,
                    () -> T.callStatic(C, "binarySearch", null, 1, 0, 0));
        });

        T.test("reverse", "test_reverse_values", "reverse", () -> {
            T.method(C, "reverse", 1);
            T.expect("reverse(\"hello\") should be \"olleh\"", "olleh", () -> T.callStatic(C, "reverse", "hello"));
            T.expect("reverse(\"ab cd\") should be \"dc ba\"", "dc ba", () -> T.callStatic(C, "reverse", "ab cd"));
            T.expect("reverse(\"racecar\") should be \"racecar\"", "racecar", () -> T.callStatic(C, "reverse", "racecar"));
            T.expect("reverse(\"ab\") should be \"ba\"", "ba", () -> T.callStatic(C, "reverse", "ab"));
            T.expect("reverse(\"a\") should be \"a\"", "a", () -> T.callStatic(C, "reverse", "a"));
            T.expect("reverse(\"\") should be \"\"", "", () -> T.callStatic(C, "reverse", ""));
            T.expectThrows("reverse(null) should throw IllegalArgumentException", IAE, () -> T.callStatic(C, "reverse", (Object) null));
        });

        T.test("fib", "test_fib_values", "fib", () -> {
            T.method(C, "fib", 1);
            T.expect("fib(0) should be 0", 0L, () -> T.callStatic(C, "fib", 0));
            T.expect("fib(1) should be 1", 1L, () -> T.callStatic(C, "fib", 1));
            T.expect("fib(2) should be 1", 1L, () -> T.callStatic(C, "fib", 2));
            T.expect("fib(7) should be 13", 13L, () -> T.callStatic(C, "fib", 7));
            T.expect("fib(10) should be 55", 55L, () -> T.callStatic(C, "fib", 10));
            T.expect("fib(30) should be 832040", 832040L, () -> T.callStatic(C, "fib", 30));
            T.expectThrows("fib(-1) should throw IllegalArgumentException", IAE, () -> T.callStatic(C, "fib", -1));
        });

        T.test("tailSum", "test_tailSum_values", "tailSum", () -> {
            T.method(C, "tailSum", 2);
            T.expect("tailSum(4, 0) should be 10", 10L, () -> T.callStatic(C, "tailSum", 4, 0L));
            T.expect("tailSum(1, 0) should be 1", 1L, () -> T.callStatic(C, "tailSum", 1, 0L));
            T.expect("tailSum(3, 100) should be 106", 106L, () -> T.callStatic(C, "tailSum", 3, 100L));
            T.expect("tailSum(0, 7) should be 7", 7L, () -> T.callStatic(C, "tailSum", 0, 7L));
            T.expect("tailSum(-5, 7) should be 7", 7L, () -> T.callStatic(C, "tailSum", -5, 7L));
            T.expect("tailSum(1000, 0) should be 500500", 500500L, () -> T.callStatic(C, "tailSum", 1000, 0L));
        });

        T.test("listSum", "test_listSum_values", "listSum", () -> {
            T.method(C, "listSum", 1);
            T.expect("listSum(1 -> 2 -> 3 -> 4) should be 10", 10, () -> T.callStatic(C, "listSum", build(1, 2, 3, 4)));
            T.expect("listSum(5) should be 5", 5, () -> T.callStatic(C, "listSum", build(5)));
            T.expect("listSum(-3 -> 3 -> -1) should be -1", -1, () -> T.callStatic(C, "listSum", build(-3, 3, -1)));
            T.expect("listSum(null) should be 0", 0, () -> T.callStatic(C, "listSum", (Object) null));
            Object l = build(1, 2, 3);
            T.callStatic(C, "listSum", l);
            T.expect("listSum must not change the list", "1 -> 2 -> 3", () -> str(l));
        });

        T.test("contains", "test_contains_values", "contains", () -> {
            T.method(C, "contains", 2);
            Object l = build(5, 8, 2);
            T.expect("contains(5 -> 8 -> 2, 5) should be true (first node)", true, () -> T.callStatic(C, "contains", l, 5));
            T.expect("contains(5 -> 8 -> 2, 8) should be true", true, () -> T.callStatic(C, "contains", l, 8));
            T.expect("contains(5 -> 8 -> 2, 2) should be true (last node)", true, () -> T.callStatic(C, "contains", l, 2));
            T.expect("contains(5 -> 8 -> 2, 7) should be false", false, () -> T.callStatic(C, "contains", l, 7));
            T.expect("contains(null, 1) should be false", false, () -> T.callStatic(C, "contains", null, 1));
            T.expect("contains must not change the list", "5 -> 8 -> 2", () -> str(l));
        });

        T.test("reverseList", "test_reverseList_values", "reverseList", () -> {
            T.method(C, "reverseList", 1);
            Object l = build(1, 2, 3, 4);
            Object oldLast = T.field(T.field(T.field(l, "next"), "next"), "next");
            Object r = T.callStatic(C, "reverseList", l);
            T.expect("reverseList(1 -> 2 -> 3 -> 4) should give 4 -> 3 -> 2 -> 1", "4 -> 3 -> 2 -> 1", () -> str(r));
            T.expectSame("reverseList must return the old last node (re-link, do not copy)", oldLast, () -> r);
            T.expectNull("the old head must become the last node (its next must be null)", () -> T.field(l, "next"));
            T.expect("reverseList(1 -> 2) should give 2 -> 1", "2 -> 1", () -> str(T.callStatic(C, "reverseList", build(1, 2))));
        });

        T.test("reverseList", "test_reverseList_small", "reverseList on empty and one-node lists", () -> {
            T.expectNull("reverseList(null) should return null", () -> T.callStatic(C, "reverseList", (Object) null));
            Object one = build(9);
            T.expectSame("reverseList on a one-node list should return that node", one, () -> T.callStatic(C, "reverseList", one));
            T.expectNull("the single node's next should still be null", () -> T.field(one, "next"));
        });

        T.test("(code rules)", "test_Lab05_rules", "Lab05 code rules", () -> {
            T.expectFalse("Lab05.java must not contain loops (for, while, do): every method must be recursive",
                    T.matches(F, "\\bfor\\s*\\(") || T.matches(F, "\\bwhile\\s*\\(") || T.matches(F, "\\bdo\\s*\\{"));
            T.expectFalse("Lab05.java must not use java.util, streams or StringBuilder.reverse()",
                    T.uses(F, "java.util") || T.uses(F, "Arrays") || T.uses(F, "stream") || T.matches(F, "\\.reverse\\s*\\(\\s*\\)"));
            T.expectSilent("the required methods must not print", () -> {
                try {
                    T.callStatic(C, "factorial", 4);
                    T.callStatic(C, "sum", new int[] {1, 2}, 2);
                    T.callStatic(C, "power", 2L, 5);
                    T.callStatic(C, "binarySearch", new int[] {1, 2, 3}, 2, 0, 2);
                    T.callStatic(C, "reverse", "abc");
                    T.callStatic(C, "fib", 5);
                    T.callStatic(C, "tailSum", 3, 0L);
                    T.callStatic(C, "listSum", build(1, 2));
                    T.callStatic(C, "contains", build(1, 2), 2);
                    T.callStatic(C, "reverseList", build(1, 2));
                } catch (Throwable ignored) {
                    // printing is what matters here
                }
            });
        });

        T.done();
    }
}
