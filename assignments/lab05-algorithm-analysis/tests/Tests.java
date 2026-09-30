/** Hidden tests for Lab 5: Algorithm Analysis (school style: one test = one check). */
public class Tests {

    static final String F = "Lab05.java";
    static final Class<IllegalArgumentException> IAE = IllegalArgumentException.class;

    static Object s(String m, Object... a) {
        return T.callStatic("Lab05", m, a);
    }

    static Object arr(int... a) {
        return a;
    }

    public static void main(String[] args) {

        // ================================================================ Part 1
        T.test("countLinear", "test_countLinear_values", "countLinear values", () -> {
            T.method("Lab05", "countLinear", 1);
            T.expect("countLinear(5) should be 5", 5L, () -> s("countLinear", 5));
            T.expect("countLinear(0) should be 0", 0L, () -> s("countLinear", 0));
            T.expect("countLinear(1) should be 1", 1L, () -> s("countLinear", 1));
            T.expect("countLinear(2000000000) should be 2000000000", 2000000000L, () -> s("countLinear", 2000000000));
        });

        T.test("countLinear", "test_countLinear_invalid", "countLinear(-1)", () -> {
            T.expectThrows("countLinear(-1) should throw IllegalArgumentException", IAE, () -> s("countLinear", -1));
        });

        T.test("countConditionChecks", "test_countConditionChecks_values", "countConditionChecks values", () -> {
            T.method("Lab05", "countConditionChecks", 1);
            T.expect("countConditionChecks(5) should be 6", 6L, () -> s("countConditionChecks", 5));
            T.expect("countConditionChecks(0) should be 1 (the false check still runs)", 1L,
                    () -> s("countConditionChecks", 0));
            T.expect("countConditionChecks(1) should be 2", 2L, () -> s("countConditionChecks", 1));
            T.expect("countConditionChecks(2000000000) should be 2000000001", 2000000001L,
                    () -> s("countConditionChecks", 2000000000));
            T.expect("countConditionChecks(Integer.MAX_VALUE) should be 2147483648 (no int overflow)", 2147483648L,
                    () -> s("countConditionChecks", Integer.MAX_VALUE));
            T.expectThrows("countConditionChecks(-3) should throw IllegalArgumentException", IAE,
                    () -> s("countConditionChecks", -3));
        });

        T.test("countNested", "test_countNested_values", "countNested values", () -> {
            T.method("Lab05", "countNested", 1);
            T.expect("countNested(5) should be 25", 25L, () -> s("countNested", 5));
            T.expect("countNested(0) should be 0", 0L, () -> s("countNested", 0));
            T.expect("countNested(1) should be 1", 1L, () -> s("countNested", 1));
            T.expect("countNested(100000) should be 10000000000 (needs long arithmetic)", 10000000000L,
                    () -> s("countNested", 100000));
            T.expectThrows("countNested(-1) should throw IllegalArgumentException", IAE, () -> s("countNested", -1));
        });

        T.test("countPairs", "test_countPairs_values", "countPairs values", () -> {
            T.method("Lab05", "countPairs", 1);
            T.expect("countPairs(5) should be 10", 10L, () -> s("countPairs", 5));
            T.expect("countPairs(0) should be 0", 0L, () -> s("countPairs", 0));
            T.expect("countPairs(1) should be 0", 0L, () -> s("countPairs", 1));
            T.expect("countPairs(2) should be 1", 1L, () -> s("countPairs", 2));
            T.expect("countPairs(100) should be 4950", 4950L, () -> s("countPairs", 100));
            T.expectThrows("countPairs(-1) should throw IllegalArgumentException", IAE, () -> s("countPairs", -1));
        });

        T.test("countPairs", "test_countPairs_large", "countPairs with a large n", () -> {
            T.expect("countPairs(100000) should be 4999950000", 4999950000L, () -> s("countPairs", 100000));
            T.expect("countPairs(2000000000) should be 1999999999000000000", 1999999999000000000L,
                    () -> s("countPairs", 2000000000));
        });

        T.test("countTriangle", "test_countTriangle_values", "countTriangle values", () -> {
            T.method("Lab05", "countTriangle", 1);
            T.expect("countTriangle(5) should be 15", 15L, () -> s("countTriangle", 5));
            T.expect("countTriangle(0) should be 0", 0L, () -> s("countTriangle", 0));
            T.expect("countTriangle(1) should be 1", 1L, () -> s("countTriangle", 1));
            T.expect("countTriangle(4) should be 10", 10L, () -> s("countTriangle", 4));
            T.expectThrows("countTriangle(-1) should throw IllegalArgumentException", IAE,
                    () -> s("countTriangle", -1));
        });

        T.test("countTriangle", "test_countTriangle_large", "countTriangle with a large n", () -> {
            T.expect("countTriangle(100000) should be 5000050000", 5000050000L, () -> s("countTriangle", 100000));
            T.expect("countTriangle(2000000000) should be 2000000001000000000", 2000000001000000000L,
                    () -> s("countTriangle", 2000000000));
        });

        // ================================================================ Part 2
        T.test("countHalvings", "test_countHalvings_values", "countHalvings values", () -> {
            T.method("Lab05", "countHalvings", 1);
            T.expect("countHalvings(1) should be 0", 0, () -> s("countHalvings", 1));
            T.expect("countHalvings(2) should be 1", 1, () -> s("countHalvings", 2));
            T.expect("countHalvings(5) should be 2 (5 -> 2 -> 1)", 2, () -> s("countHalvings", 5));
            T.expect("countHalvings(1023) should be 9", 9, () -> s("countHalvings", 1023));
            T.expect("countHalvings(1024) should be 10", 10, () -> s("countHalvings", 1024));
            T.expect("countHalvings(1000000) should be 19", 19, () -> s("countHalvings", 1000000));
            T.expect("countHalvings(Integer.MAX_VALUE) should be 30", 30, () -> s("countHalvings", Integer.MAX_VALUE));
        });

        T.test("countHalvings", "test_countHalvings_invalid", "countHalvings invalid", () -> {
            T.expectThrows("countHalvings(0) should throw IllegalArgumentException", IAE, () -> s("countHalvings", 0));
            T.expectThrows("countHalvings(-8) should throw IllegalArgumentException", IAE, () -> s("countHalvings", -8));
        });

        T.test("dominantTerm", "test_dominantTerm_classes", "dominantTerm classes", () -> {
            T.method("Lab05", "dominantTerm", 4);
            T.expect("dominantTerm(8, 0, 0, 0) should be O(1)", "O(1)", () -> s("dominantTerm", 8L, 0L, 0L, 0L));
            T.expect("dominantTerm(20, 5, 0, 0) should be O(n)", "O(n)", () -> s("dominantTerm", 20L, 5L, 0L, 0L));
            T.expect("dominantTerm(500, 100, 3, 0) should be O(n^2)", "O(n^2)",
                    () -> s("dominantTerm", 500L, 100L, 3L, 0L));
            T.expect("dominantTerm(0, 0, 1000000, 1) should be O(n^3)", "O(n^3)",
                    () -> s("dominantTerm", 0L, 0L, 1000000L, 1L));
        });

        T.test("dominantTerm", "test_dominantTerm_edge", "dominantTerm edge cases", () -> {
            T.expect("dominantTerm(0, 0, 0, 0) should be O(1)", "O(1)", () -> s("dominantTerm", 0L, 0L, 0L, 0L));
            T.expect("dominantTerm(0, 1, 0, 0) should be O(n) even with a zero constant", "O(n)",
                    () -> s("dominantTerm", 0L, 1L, 0L, 0L));
            T.expect("dominantTerm(5, 0, 7, 0) should be O(n^2) (a zero c1 does not matter)", "O(n^2)",
                    () -> s("dominantTerm", 5L, 0L, 7L, 0L));
            T.expect("dominantTerm(9, -4, 0, 0) should be O(n) (a negative coefficient still counts)", "O(n)",
                    () -> s("dominantTerm", 9L, -4L, 0L, 0L));
            T.expect("dominantTerm(1, 2, 3, -1) should be O(n^3)", "O(n^3)", () -> s("dominantTerm", 1L, 2L, 3L, -1L));
            T.expect("dominantTerm(3000000000, 0, 0, 0) should be O(1) for a huge constant", "O(1)",
                    () -> s("dominantTerm", 3000000000L, 0L, 0L, 0L));
        });

        // ================================================================ Part 3
        T.test("countPairsWithSum", "test_countPairsWithSum_basic", "countPairsWithSum basic", () -> {
            T.method("Lab05", "countPairsWithSum", 2);
            Object a = arr(1, 2, 3, 4, 5, 6);
            T.expect("countPairsWithSum([1, 2, 3, 4, 5, 6], 7) should be 3", 3, () -> s("countPairsWithSum", a, 7));
            T.expect("countPairsWithSum([1, 2, 3, 4, 5, 6], 3) should be 1", 1, () -> s("countPairsWithSum", a, 3));
            T.expect("countPairsWithSum([1, 2, 3, 4, 5, 6], 100) should be 0", 0, () -> s("countPairsWithSum", a, 100));
            T.expect("countPairsWithSum([1, 2, 3, 4, 5, 6], 1) should be 0 (no pair sums to 1)", 0,
                    () -> s("countPairsWithSum", a, 1));
            T.expect("countPairsWithSum([2, 4, 6, 8], 10) should be 2 (2+8, 4+6)", 2,
                    () -> s("countPairsWithSum", arr(2, 4, 6, 8), 10));
        });

        T.test("countPairsWithSum", "test_countPairsWithSum_edge", "countPairsWithSum edge cases", () -> {
            T.expect("countPairsWithSum([], 0) should be 0", 0, () -> s("countPairsWithSum", arr(), 0));
            T.expect("countPairsWithSum([5], 10) should be 0 (an element cannot pair with itself)", 0,
                    () -> s("countPairsWithSum", arr(5), 10));
            T.expect("countPairsWithSum([-3, -1, 0, 1, 3], 0) should be 2 (-3+3, -1+1)", 2,
                    () -> s("countPairsWithSum", arr(-3, -1, 0, 1, 3), 0));
            T.expect("countPairsWithSum([1, 2], 3) should be 1", 1, () -> s("countPairsWithSum", arr(1, 2), 3));
            T.expect("countPairsWithSum near Integer.MAX_VALUE must not overflow", 0,
                    () -> s("countPairsWithSum", arr(1, Integer.MAX_VALUE - 1, Integer.MAX_VALUE), -2));
            T.expect("countPairsWithSum with a sum above Integer.MAX_VALUE must not overflow", 0,
                    () -> s("countPairsWithSum", arr(Integer.MAX_VALUE - 1, Integer.MAX_VALUE), -3));
            Object a = arr(1, 2, 3, 4);
            s("countPairsWithSum", a, 5);
            T.expect("countPairsWithSum must not change the array", "[1, 2, 3, 4]", () -> T.text(a));
        });

        T.test("countPairsWithSum", "test_countPairsWithSum_invalid", "countPairsWithSum invalid input", () -> {
            T.expectThrows("countPairsWithSum(null, 5) should throw IllegalArgumentException", IAE,
                    () -> s("countPairsWithSum", (Object) null, 5));
            T.expectThrows("countPairsWithSum([3, 1, 2], 4) should throw IllegalArgumentException (not sorted)", IAE,
                    () -> s("countPairsWithSum", arr(3, 1, 2), 4));
            T.expectThrows("countPairsWithSum([1, 1, 2], 3) should throw IllegalArgumentException (duplicate)", IAE,
                    () -> s("countPairsWithSum", arr(1, 1, 2), 3));
        });

        T.test("twoPointerComparisons", "test_twoPointerComparisons_values", "twoPointerComparisons values", () -> {
            T.method("Lab05", "twoPointerComparisons", 2);
            Object a = arr(1, 2, 3, 4, 5, 6);
            T.expect("twoPointerComparisons([1, 2, 3, 4, 5, 6], 7) should be 3", 3,
                    () -> s("twoPointerComparisons", a, 7));
            T.expect("twoPointerComparisons([1, 2, 3, 4, 5, 6], 3) should be 5", 5,
                    () -> s("twoPointerComparisons", a, 3));
            T.expect("twoPointerComparisons([1, 2, 3, 4, 5, 6], 100) should be 5", 5,
                    () -> s("twoPointerComparisons", a, 100));
            T.expect("twoPointerComparisons([1, 2, 3, 4, 5, 6], 1) should be 5", 5,
                    () -> s("twoPointerComparisons", a, 1));
            T.expect("twoPointerComparisons([2, 4, 6, 8], 10) should be 2", 2,
                    () -> s("twoPointerComparisons", arr(2, 4, 6, 8), 10));
        });

        T.test("twoPointerComparisons", "test_twoPointerComparisons_edge", "twoPointerComparisons edge cases", () -> {
            T.expect("twoPointerComparisons([], 0) should be 0", 0, () -> s("twoPointerComparisons", arr(), 0));
            T.expect("twoPointerComparisons([5], 5) should be 0", 0, () -> s("twoPointerComparisons", arr(5), 5));
            T.expect("twoPointerComparisons([1, 2], 3) should be 1", 1, () -> s("twoPointerComparisons", arr(1, 2), 3));
            int[] big = new int[1000];
            for (int i = 0; i < big.length; i++) {
                big[i] = i;
            }
            T.expect("twoPointerComparisons on 1000 elements with no pair should be 999 (n - 1)", 999,
                    () -> s("twoPointerComparisons", big, 5000));
            T.expectTrue("twoPointerComparisons on 1000 elements must never exceed 999", () -> {
                for (int t = -5; t < 2100; t += 7) {
                    if ((Integer) s("twoPointerComparisons", big, t) > 999) {
                        return false;
                    }
                }
                return true;
            });
            T.expectThrows("twoPointerComparisons(null, 5) should throw IllegalArgumentException", IAE,
                    () -> s("twoPointerComparisons", (Object) null, 5));
            T.expectThrows("twoPointerComparisons([2, 2], 4) should throw IllegalArgumentException", IAE,
                    () -> s("twoPointerComparisons", arr(2, 2), 4));
        });

        // ================================================================ rules
        T.test("(code rules)", "test_Lab05_rules", "Lab05 code rules", () -> {
            T.expectFalse("Lab05.java must not import or use java.util", T.matches(F, "java\\s*\\.\\s*util"));
            T.expectSilent("the required methods must not print", () -> {
                try {
                    s("countLinear", 5);
                    s("countConditionChecks", 5);
                    s("countNested", 5);
                    s("countPairs", 5);
                    s("countTriangle", 5);
                    s("countHalvings", 5);
                    s("dominantTerm", 1L, 2L, 3L, 4L);
                    s("countPairsWithSum", arr(1, 2, 3), 4);
                    s("twoPointerComparisons", arr(1, 2, 3), 4);
                } catch (Throwable ignored) {
                    // printing is what matters here
                }
            });
        });

        T.done();
    }
}
