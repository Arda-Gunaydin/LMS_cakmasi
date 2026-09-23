/** Hidden tests for Lab 2: Arrays and the ScoreBook (school style: one test = one check). */
public class Tests {

    static final String F = "Lab02.java";
    static final Class<IllegalArgumentException> IAE = IllegalArgumentException.class;
    static final Class<IndexOutOfBoundsException> IOOBE = IndexOutOfBoundsException.class;

    static Object tool(String method, Object... args) {
        return T.callStatic("ArrayTools", method, args);
    }

    static Object book(int... scores) {
        Object b = T.make("ScoreBook", 2);
        for (int s : scores) {
            T.call(b, "add", s);
        }
        return b;
    }

    static int[] contents(Object b) {
        int n = (Integer) T.call(b, "size");
        int[] out = new int[n];
        for (int i = 0; i < n; i++) {
            out[i] = (Integer) T.call(b, "get", i);
        }
        return out;
    }

    public static void main(String[] args) {

        // ================================================================ ArrayTools
        T.test("ArrayTools", "test_ArrayTools_sum", "sum", () -> {
            T.expect("sum([3, 4, 5]) should be 12", 12, () -> tool("sum", (Object) new int[] { 3, 4, 5 }));
            T.expect("sum([]) should be 0", 0, () -> tool("sum", (Object) new int[0]));
            T.expectThrows("sum(null) should throw IllegalArgumentException", IAE, () -> tool("sum", (Object) null));
        });

        T.test("ArrayTools", "test_ArrayTools_average", "average", () -> {
            T.expect("average([1, 2]) should be 1.5", 1.5, () -> tool("average", (Object) new int[] { 1, 2 }));
            T.expect("average([7]) should be 7.0", 7.0, () -> tool("average", (Object) new int[] { 7 }));
            T.expectThrows("average([]) should throw IllegalArgumentException", IAE,
                    () -> tool("average", (Object) new int[0]));
            T.expectThrows("average(null) should throw IllegalArgumentException", IAE,
                    () -> tool("average", (Object) null));
        });

        T.test("ArrayTools", "test_ArrayTools_indexOfMax", "indexOfMax", () -> {
            T.expect("indexOfMax([4, 9, 2, 9]) should be 1 (the first of two maxima)", 1,
                    () -> tool("indexOfMax", (Object) new int[] { 4, 9, 2, 9 }));
            T.expect("indexOfMax([-5, -2, -9]) should be 1 (all negative)", 1,
                    () -> tool("indexOfMax", (Object) new int[] { -5, -2, -9 }));
            T.expect("indexOfMax([8]) should be 0", 0, () -> tool("indexOfMax", (Object) new int[] { 8 }));
            T.expectThrows("indexOfMax([]) should throw IllegalArgumentException", IAE,
                    () -> tool("indexOfMax", (Object) new int[0]));
        });

        T.test("ArrayTools", "test_ArrayTools_reversed", "reversed", () -> {
            int[] in = { 1, 2, 3 };
            T.expect("reversed([1, 2, 3]) should be [3, 2, 1]", new int[] { 3, 2, 1 }, () -> tool("reversed", (Object) in));
            T.expect("reversed must leave its input [1, 2, 3] unchanged", new int[] { 1, 2, 3 }, () -> in);
            T.expectTrue("reversed must return a new array", () -> tool("reversed", (Object) in) != in);
            T.expect("reversed([]) should be []", new int[0], () -> tool("reversed", (Object) new int[0]));
        });

        T.test("ArrayTools", "test_ArrayTools_countInRange", "countInRange", () -> {
            T.expect("countInRange([50, 60, 70, 80], 60, 70) should be 2 (both ends included)", 2,
                    () -> tool("countInRange", new int[] { 50, 60, 70, 80 }, 60, 70));
            T.expect("countInRange([1, 2], 5, 9) should be 0", 0,
                    () -> tool("countInRange", new int[] { 1, 2 }, 5, 9));
            T.expectThrows("countInRange(a, 10, 5) should throw IllegalArgumentException", IAE,
                    () -> tool("countInRange", new int[] { 7 }, 10, 5));
            T.expectThrows("countInRange(null, 0, 1) should throw IllegalArgumentException", IAE,
                    () -> tool("countInRange", null, 0, 1));
        });

        T.test("ArrayTools", "test_ArrayTools_withoutDuplicates", "withoutDuplicates", () -> {
            int[] in = { 3, 1, 3, 2, 1 };
            T.expect("withoutDuplicates([3, 1, 3, 2, 1]) should be [3, 1, 2]", new int[] { 3, 1, 2 },
                    () -> tool("withoutDuplicates", (Object) in));
            T.expect("withoutDuplicates must leave its input unchanged", new int[] { 3, 1, 3, 2, 1 }, () -> in);
            T.expect("withoutDuplicates([]) should be []", new int[0], () -> tool("withoutDuplicates", (Object) new int[0]));
            T.expect("withoutDuplicates([5, 5, 5]) should be [5]", new int[] { 5 },
                    () -> tool("withoutDuplicates", (Object) new int[] { 5, 5, 5 }));
        });

        T.test("ArrayTools", "test_ArrayTools_rotateLeft", "rotateLeft", () -> {
            T.expect("rotateLeft([1, 2, 3, 4, 5], 2) should give [3, 4, 5, 1, 2]", new int[] { 3, 4, 5, 1, 2 }, () -> {
                int[] a = { 1, 2, 3, 4, 5 };
                tool("rotateLeft", a, 2);
                return a;
            });
            T.expect("rotateLeft([1, 2, 3], 7) should give [2, 3, 1]", new int[] { 2, 3, 1 }, () -> {
                int[] a = { 1, 2, 3 };
                tool("rotateLeft", a, 7);
                return a;
            });
            T.expect("rotateLeft([], 3) should leave the empty array as it is", new int[0], () -> {
                int[] a = new int[0];
                tool("rotateLeft", a, 3);
                return a;
            });
            T.expectThrows("rotateLeft(a, -1) should throw IllegalArgumentException", IAE,
                    () -> tool("rotateLeft", new int[] { 1, 2 }, -1));
        });

        T.test("ArrayTools", "test_ArrayTools_transpose", "transpose", () -> {
            int[][] m = { { 1, 2, 3 }, { 4, 5, 6 } };
            T.expect("transpose([[1, 2, 3], [4, 5, 6]]) should be [[1, 4], [2, 5], [3, 6]]",
                    new int[][] { { 1, 4 }, { 2, 5 }, { 3, 6 } }, () -> tool("transpose", (Object) m));
            T.expect("transpose must leave its input unchanged", new int[][] { { 1, 2, 3 }, { 4, 5, 6 } }, () -> m);
            T.expectThrows("transpose of a jagged array should throw IllegalArgumentException", IAE,
                    () -> tool("transpose", (Object) new int[][] { { 1, 2 }, { 3 } }));
            T.expectThrows("transpose of an empty array should throw IllegalArgumentException", IAE,
                    () -> tool("transpose", (Object) new int[0][]));
            T.expectThrows("transpose with a null row should throw IllegalArgumentException", IAE,
                    () -> tool("transpose", (Object) new int[][] { { 1 }, null }));
        });

        // ================================================================ ScoreBook
        T.test("ScoreBook", "test_ScoreBook_structure", "ScoreBook structure", () -> {
            T.expectTrue("all ScoreBook fields must be private", T.fieldsPrivate("ScoreBook"));
            T.expectTrue("ScoreBook must store the scores in a private int[] field", () -> {
                for (java.lang.reflect.Field f : T.cls("ScoreBook").getDeclaredFields()) {
                    if (f.getType() == int[].class) {
                        return true;
                    }
                }
                return false;
            });
        });

        T.test("ScoreBook", "test_ScoreBook_constructor", "ScoreBook constructors", () -> {
            T.expect("new ScoreBook().size() should be 0", 0, () -> T.call(T.make("ScoreBook"), "size"));
            T.expect("new ScoreBook().capacity() should be 4", 4, () -> T.call(T.make("ScoreBook"), "capacity"));
            T.expect("new ScoreBook(1).capacity() should be 1", 1, () -> T.call(T.make("ScoreBook", 1), "capacity"));
            T.expectThrows("new ScoreBook(0) should throw IllegalArgumentException", IAE, () -> T.make("ScoreBook", 0));
            T.expectThrows("new ScoreBook(-3) should throw IllegalArgumentException", IAE, () -> T.make("ScoreBook", -3));
        });

        T.test("ScoreBook", "test_ScoreBook_add", "add and growing", () -> {
            T.method("ScoreBook", "add", 1);
            Object b = book(90, 75, 60);
            T.expect("adding 3 scores to new ScoreBook(2) should give size 3", 3, () -> T.call(b, "size"));
            T.expect("the full array should double: capacity 2 -> 4", 4, () -> T.call(b, "capacity"));
            T.expect("the scores should keep their order", new int[] { 90, 75, 60 }, () -> contents(b));
            T.expect("adding 9 scores to new ScoreBook(1) should give capacity 16", 16, () -> {
                Object g = T.make("ScoreBook", 1);
                for (int i = 1; i <= 9; i++) {
                    T.call(g, "add", i * 10);
                }
                return T.call(g, "capacity");
            });
            T.expectThrows("add(101) should throw IllegalArgumentException", IAE, () -> T.call(b, "add", 101));
            T.expectThrows("add(-1) should throw IllegalArgumentException", IAE, () -> T.call(b, "add", -1));
            T.expect("rejected adds must leave the book unchanged", new int[] { 90, 75, 60 }, () -> contents(b));
        });

        T.test("ScoreBook", "test_ScoreBook_getSet", "get and set", () -> {
            T.method("ScoreBook", "add", 1);
            Object b = book(90, 75, 60);
            T.expect("get(0) should be 90", 90, () -> T.call(b, "get", 0));
            T.expectThrows("get(3) with size 3 (capacity 4) should throw IndexOutOfBoundsException", IOOBE,
                    () -> T.call(b, "get", 3));
            T.expectThrows("get(-1) should throw IndexOutOfBoundsException", IOOBE, () -> T.call(b, "get", -1));
            T.expectNoThrow("set(1, 100) should be accepted", () -> T.call(b, "set", 1, 100));
            T.expectThrows("set(0, 120) should throw IllegalArgumentException", IAE, () -> T.call(b, "set", 0, 120));
            T.expectThrows("set(3, 50) should throw IndexOutOfBoundsException", IOOBE, () -> T.call(b, "set", 3, 50));
            T.expect("after set(1, 100) the book should be [90, 100, 60]", new int[] { 90, 100, 60 }, () -> contents(b));
        });

        T.test("ScoreBook", "test_ScoreBook_insertAt", "insertAt", () -> {
            T.method("ScoreBook", "add", 1);
            T.expect("insertAt(0, 50) on [90, 75] should give [50, 90, 75]", new int[] { 50, 90, 75 }, () -> {
                Object b = book(90, 75);
                T.call(b, "insertAt", 0, 50);
                return contents(b);
            });
            T.expect("insertAt into a full ScoreBook(2) should double the capacity to 4", 4, () -> {
                Object b = book(90, 75);
                T.call(b, "insertAt", 1, 50);
                return T.call(b, "capacity");
            });
            T.expect("insertAt(size(), 40) should append", new int[] { 90, 75, 40 }, () -> {
                Object b = book(90, 75);
                T.call(b, "insertAt", 2, 40);
                return contents(b);
            });
            T.expectThrows("insertAt(size() + 1, 40) should throw IndexOutOfBoundsException", IOOBE,
                    () -> T.call(book(90, 75), "insertAt", 3, 40));
            T.expectThrows("insertAt(0, 101) should throw IllegalArgumentException", IAE,
                    () -> T.call(book(90, 75), "insertAt", 0, 101));
        });

        T.test("ScoreBook", "test_ScoreBook_removeAt", "removeAt", () -> {
            T.method("ScoreBook", "add", 1);
            Object b = book(90, 75, 60);
            T.expect("removeAt(1) on [90, 75, 60] should return 75", 75, () -> T.call(b, "removeAt", 1));
            T.expect("after removeAt(1) the book should be [90, 60]", new int[] { 90, 60 }, () -> contents(b));
            T.expectThrows("removeAt(-1) should throw IndexOutOfBoundsException", IOOBE,
                    () -> T.call(b, "removeAt", -1));
            T.expectThrows("removeAt(size()) should throw IndexOutOfBoundsException", IOOBE,
                    () -> T.call(b, "removeAt", 2));
            T.expect("removing must not shrink the capacity", 4, () -> T.call(b, "capacity"));
        });

        T.test("ScoreBook", "test_ScoreBook_toArray", "toArray", () -> {
            T.method("ScoreBook", "add", 1);
            Object b = book(90, 75, 60);
            T.expect("toArray() should be [90, 75, 60] with exactly size() elements", new int[] { 90, 75, 60 },
                    () -> T.call(b, "toArray"));
            T.expect("changing the array from toArray() must not change the book", 90, () -> {
                int[] arr = (int[]) T.call(b, "toArray");
                arr[0] = 1;
                return T.call(b, "get", 0);
            });
            T.expect("toArray() of an empty book should be []", new int[0], () -> T.call(T.make("ScoreBook"), "toArray"));
        });

        T.test("ScoreBook", "test_ScoreBook_toString", "toString", () -> {
            T.method("ScoreBook", "add", 1);
            T.expect("toString() of [90, 75, 60]", "[90, 75, 60]", () -> T.call(book(90, 75, 60), "toString"));
            T.expect("toString() of an empty book", "[]", () -> T.call(T.make("ScoreBook"), "toString"));
            T.expect("toString() of [100]", "[100]", () -> T.call(book(100), "toString"));
        });

        T.test("ScoreBook", "test_ScoreBook_letterCounts", "letterCounts", () -> {
            T.method("ScoreBook", "add", 1);
            T.expect("letterCounts() of [100, 90, 89, 70, 69, 60, 59, 0] should be [2, 1, 1, 2, 2]",
                    new int[] { 2, 1, 1, 2, 2 }, () -> T.call(book(100, 90, 89, 70, 69, 60, 59, 0), "letterCounts"));
            T.expect("letterCounts() of an empty book should be [0, 0, 0, 0, 0]", new int[5],
                    () -> T.call(T.make("ScoreBook"), "letterCounts"));
        });

        // ================================================================ rules
        T.test("(code rules)", "test_Lab02_rules", "Lab02 code rules", () -> {
            T.expectFalse("only Lab02 may be public", T.isPublic("ArrayTools") || T.isPublic("ScoreBook"));
            T.expectFalse("Lab02.java must not use java.util", T.matches(F, "java\\s*\\.\\s*util"));
            T.expectFalse("Lab02.java must not use Arrays or ArrayList", T.uses(F, "Arrays") || T.uses(F, "ArrayList"));
            T.expectFalse("Lab02.java must not use System.arraycopy", T.uses(F, "arraycopy"));
            T.expectFalse("Lab02.java must not use clone()", T.uses(F, "clone"));
            T.expectSilent("the required methods must not print", () -> {
                try {
                    tool("sum", (Object) new int[] { 1 });
                    tool("reversed", (Object) new int[] { 1, 2 });
                    Object b = book(90, 75, 60);
                    T.call(b, "insertAt", 0, 10);
                    T.call(b, "removeAt", 0);
                    T.call(b, "toString");
                    T.call(b, "letterCounts");
                } catch (Throwable ignored) {
                    // printing is what matters here
                }
            });
        });

        T.done();
    }
}
