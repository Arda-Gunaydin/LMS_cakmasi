/** Hidden tests for Lab 4: Binary Search Trees (school style: one test = one check). */
public class Tests {

    static final String F = "Lab04.java";
    static final Class<IllegalArgumentException> IAE = IllegalArgumentException.class;
    static final Class<java.util.NoSuchElementException> NSEE = java.util.NoSuchElementException.class;

    static Object treeOf(Object... values) {
        Object t = T.make("BST");
        for (Object v : values) {
            T.call(t, "insert", v);
        }
        return t;
    }

    public static void main(String[] args) {

        // ================================================================ structure
        T.test("BST", "test_BST_structure", "BST structure", () -> {
            T.expectTrue("all BST fields must be private", T.fieldsPrivate("BST"));
        });

        T.test("BST", "test_BST_constructorEmpty", "empty BST", () -> {
            Object t = T.make("BST");
            T.expect("new BST().size() should be 0", 0, () -> T.call(t, "size"));
            T.expectTrue("new BST().isEmpty() should be true", () -> T.call(t, "isEmpty"));
            T.expect("new BST().height() should be -1", -1, () -> T.call(t, "height"));
            T.expect("new BST().toString() should be []", "[]", () -> T.call(t, "toString"));
            T.expect("new BST().contains(1) should be false", false, () -> T.call(t, "contains", 1));
        });

        // ================================================================ insert
        T.test("insert", "test_insert_basic", "insert and ordering", () -> {
            T.method("BST", "insert", 1);
            Object t = T.make("BST");
            T.expect("insert(5) on an empty tree should return true", true, () -> T.call(t, "insert", 5));
            T.expect("after insert(5), size() should be 1", 1, () -> T.call(t, "size"));
            T.expect("after insert(5), isEmpty() should be false", false, () -> T.call(t, "isEmpty"));
            T.expect("after insert(5), height() should be 0", 0, () -> T.call(t, "height"));
            T.call(t, "insert", 2);
            T.call(t, "insert", 8);
            T.call(t, "insert", 1);
            T.expect("in-order toString() should list values ascending: [1, 2, 5, 8]", "[1, 2, 5, 8]",
                    () -> T.call(t, "toString"));
            T.expect("size() should be 4 after 4 distinct inserts", 4, () -> T.call(t, "size"));
        });

        T.test("insert", "test_insert_duplicate", "insert of an existing value", () -> {
            Object t = treeOf(5, 2, 8);
            T.expect("insert(5) when 5 is already present should return false", false, () -> T.call(t, "insert", 5));
            T.expect("size() should stay 3 after inserting a duplicate", 3, () -> T.call(t, "size"));
            T.expect("toString() should be unchanged after inserting a duplicate", "[2, 5, 8]",
                    () -> T.call(t, "toString"));
        });

        T.test("insert", "test_insert_chain", "insert building an unbalanced chain", () -> {
            Object t = treeOf(1, 2, 3, 4, 5);
            T.expect("inserting 1..5 in increasing order should give height 4 (a chain)", 4,
                    () -> T.call(t, "height"));
            T.expect("size() should be 5", 5, () -> T.call(t, "size"));
            T.expect("toString() should still be ascending: [1, 2, 3, 4, 5]", "[1, 2, 3, 4, 5]",
                    () -> T.call(t, "toString"));
        });

        T.test("insert", "test_insert_null", "insert(null)", () -> {
            T.expectThrows("insert(null) should throw IllegalArgumentException", IAE,
                    () -> T.call(T.make("BST"), "insert", (Object) null));
        });

        // ================================================================ contains
        T.test("contains", "test_contains_found", "contains on present and absent values", () -> {
            T.method("BST", "contains", 1);
            Object t = treeOf(5, 2, 8, 1, 9);
            T.expectTrue("contains(8) should be true", () -> T.call(t, "contains", 8));
            T.expectTrue("contains(1) should be true", () -> T.call(t, "contains", 1));
            T.expect("contains(3) should be false (3 was never inserted)", false, () -> T.call(t, "contains", 3));
            T.expect("contains on an empty tree should be false", false, () -> T.call(T.make("BST"), "contains", 1));
        });

        T.test("contains", "test_contains_null", "contains(null)", () -> {
            T.expectThrows("contains(null) should throw IllegalArgumentException", IAE,
                    () -> T.call(T.make("BST"), "contains", (Object) null));
        });

        // ================================================================ min / max
        T.test("min", "test_min_value", "min", () -> {
            T.method("BST", "min", 0);
            Object t = treeOf(5, 2, 8, 1, 9, 3);
            T.expect("min() should be 1", 1, () -> T.call(t, "min"));
            T.expectThrows("min() on an empty tree should throw NoSuchElementException", NSEE,
                    () -> T.call(T.make("BST"), "min"));
        });

        T.test("max", "test_max_value", "max", () -> {
            T.method("BST", "max", 0);
            Object t = treeOf(5, 2, 8, 1, 9, 3);
            T.expect("max() should be 9", 9, () -> T.call(t, "max"));
            T.expectThrows("max() on an empty tree should throw NoSuchElementException", NSEE,
                    () -> T.call(T.make("BST"), "max"));
        });

        // ================================================================ remove
        T.test("remove", "test_remove_leaf", "remove a leaf", () -> {
            T.method("BST", "remove", 1);
            Object t = treeOf(5, 2, 8, 1);
            T.expect("remove(1) should return true (1 is a leaf)", true, () -> T.call(t, "remove", 1));
            T.expect("size() should drop to 3", 3, () -> T.call(t, "size"));
            T.expect("toString() should be [2, 5, 8] after removing the leaf 1", "[2, 5, 8]",
                    () -> T.call(t, "toString"));
        });

        T.test("remove", "test_remove_oneChild", "remove a node with one child", () -> {
            Object t = treeOf(5, 2, 8, 1);
            T.expect("remove(2) should return true (2 has exactly one child, 1)", true, () -> T.call(t, "remove", 2));
            T.expect("toString() should be [1, 5, 8] after removing 2", "[1, 5, 8]", () -> T.call(t, "toString"));
            T.expectTrue("1 should still be reachable after removing its parent 2", () -> T.call(t, "contains", 1));
        });

        T.test("remove", "test_remove_twoChildren", "remove a node with two children", () -> {
            Object t = treeOf(5, 2, 8, 1, 3, 7, 9);
            T.expect("remove(2) should return true (2 has two children: 1 and 3)", true, () -> T.call(t, "remove", 2));
            T.expect("toString() should be [1, 3, 5, 7, 8, 9] after removing 2, keeping BST order", "[1, 3, 5, 7, 8, 9]",
                    () -> T.call(t, "toString"));
            T.expect("size() should drop to 6", 6, () -> T.call(t, "size"));
        });

        T.test("remove", "test_remove_root", "remove the root", () -> {
            Object t = treeOf(5, 2, 8, 1, 3, 7, 9);
            T.expect("remove(5) on the root should return true", true, () -> T.call(t, "remove", 5));
            T.expect("toString() should still be sorted after removing the root: [1, 2, 3, 7, 8, 9]",
                    "[1, 2, 3, 7, 8, 9]", () -> T.call(t, "toString"));
            T.expect("size() should drop to 6", 6, () -> T.call(t, "size"));
            T.expectFalse("contains(5) should be false after removing 5", (Boolean) T.call(t, "contains", 5));
        });

        T.test("remove", "test_remove_missing", "remove a value not in the tree", () -> {
            Object t = treeOf(5, 2, 8);
            T.expect("remove(100) should return false (100 was never inserted)", false, () -> T.call(t, "remove", 100));
            T.expect("size() should stay 3", 3, () -> T.call(t, "size"));
            T.expect("toString() should be unchanged: [2, 5, 8]", "[2, 5, 8]", () -> T.call(t, "toString"));
            T.expect("remove on an empty tree should return false", false, () -> T.call(T.make("BST"), "remove", 1));
        });

        T.test("remove", "test_remove_null", "remove(null)", () -> {
            T.expectThrows("remove(null) should throw IllegalArgumentException", IAE,
                    () -> T.call(T.make("BST"), "remove", (Object) null));
        });

        T.test("remove", "test_remove_toEmpty", "remove down to an empty tree", () -> {
            Object t = treeOf(5);
            T.expect("removing the only value should return true", true, () -> T.call(t, "remove", 5));
            T.expect("size() should be 0", 0, () -> T.call(t, "size"));
            T.expectTrue("isEmpty() should be true", () -> T.call(t, "isEmpty"));
            T.expect("height() should be -1 again", -1, () -> T.call(t, "height"));
            T.expect("toString() should be []", "[]", () -> T.call(t, "toString"));
        });

        // ================================================================ toString / height
        T.test("toString", "test_toString_format", "toString formatting", () -> {
            T.expect("toString() of a new BST should be []", "[]", () -> T.call(T.make("BST"), "toString"));
            T.expect("toString() of a single-value tree [5]", "[5]", () -> T.call(treeOf(5), "toString"));
        });

        T.test("height", "test_height_shapes", "height of various shapes", () -> {
            T.expect("height() of a single node should be 0", 0, () -> T.call(treeOf(5), "height"));
            T.expect("height() of a root with 2 children should be 1", 1, () -> T.call(treeOf(5, 2, 8), "height"));
            T.expect("height() of a decreasing chain 9,7,5,3,1 should be 4", 4,
                    () -> T.call(treeOf(9, 7, 5, 3, 1), "height"));
        });

        // ================================================================ rules
        T.test("(code rules)", "test_Lab04_rules", "Lab04 code rules", () -> {
            T.expectFalse("only Lab04 may be public", T.isPublic("BST"));
            T.expectFalse(
                    "Lab04.java must not use a java.util collection (ArrayList, LinkedList, HashMap, HashSet, TreeMap, TreeSet, Stack, Deque, ArrayDeque, Vector, PriorityQueue)",
                    T.uses(F, "ArrayList") || T.uses(F, "LinkedList") || T.uses(F, "HashMap") || T.uses(F, "HashSet")
                            || T.uses(F, "TreeMap") || T.uses(F, "TreeSet") || T.uses(F, "Stack") || T.uses(F, "Deque")
                            || T.uses(F, "ArrayDeque") || T.uses(F, "Vector") || T.uses(F, "PriorityQueue"));
            T.expectSilent("the required methods must not print", () -> {
                try {
                    Object t = treeOf(5, 2, 8, 1);
                    T.call(t, "contains", 2);
                    T.call(t, "min");
                    T.call(t, "max");
                    T.call(t, "height");
                    T.call(t, "toString");
                    T.call(t, "remove", 2);
                } catch (Throwable ignored) {
                    // printing is what matters here
                }
            });
        });

        T.done();
    }
}
