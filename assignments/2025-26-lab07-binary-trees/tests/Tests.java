import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Hidden tests for last year's Lab 7: Binary Trees (school style: one test = one check). */
public class Tests {

    static final String F = "Lab07.java";

    static Object tree(String cls, Object... items) {
        Object t = cls.equals("ArrayBT") ? T.make("ArrayBT", 100) : T.make("LinkedBT");
        for (Object x : items) {
            T.call(t, "insert", x);
        }
        return t;
    }

    static String trav(Object t, String how) {
        List<Object> l = new ArrayList<>();
        T.call(t, how, l);
        return l.toString();
    }

    /** The shape of a LinkedBT as text: 1(2(4,5),3(6,-)). */
    static String shape(Object node) {
        if (node == null) {
            return "-";
        }
        Object l = T.field(node, "left");
        Object r = T.field(node, "right");
        String d = String.valueOf(T.field(node, "data"));
        return l == null && r == null ? d : d + "(" + shape(l) + "," + shape(r) + ")";
    }

    static void traversals(String c) {
        T.method(c, "preorder", 1);
        Object t = tree(c, 1, 2, 3, 4, 5, 6);
        T.expect("preorder of 1..6 should be [1, 2, 4, 5, 3, 6]", "[1, 2, 4, 5, 3, 6]", () -> trav(t, "preorder"));
        T.expect("inorder of 1..6 should be [4, 2, 5, 1, 6, 3]", "[4, 2, 5, 1, 6, 3]", () -> trav(t, "inorder"));
        T.expect("postorder of 1..6 should be [4, 5, 2, 6, 3, 1]", "[4, 5, 2, 6, 3, 1]", () -> trav(t, "postorder"));
        Object u = tree(c, "a", "b", "c", "d", "e", "f", "g", "h");
        T.expect("preorder of a..h should be [a, b, d, h, e, c, f, g]", "[a, b, d, h, e, c, f, g]", () -> trav(u, "preorder"));
        T.expect("inorder of a..h should be [h, d, b, e, a, f, c, g]", "[h, d, b, e, a, f, c, g]", () -> trav(u, "inorder"));
        T.expect("postorder of a..h should be [h, d, e, b, f, g, c, a]", "[h, d, e, b, f, g, c, a]", () -> trav(u, "postorder"));
        T.expect("a one-element tree: preorder should be [7]", "[7]", () -> trav(tree(c, 7), "preorder"));
    }

    static void bfsDfs(String c) {
        T.method(c, "BFS", 1);
        Object t = tree(c, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        T.expect("BFS of 1..9 should be [1, 2, 3, 4, 5, 6, 7, 8, 9]", "[1, 2, 3, 4, 5, 6, 7, 8, 9]", () -> trav(t, "BFS"));
        T.expect("DFS of 1..9 should be the preorder [1, 2, 4, 8, 9, 5, 3, 6, 7]", "[1, 2, 4, 8, 9, 5, 3, 6, 7]",
                () -> trav(t, "DFS"));
        List<Object> l = new ArrayList<>(Arrays.asList("x", "y"));
        T.call(tree(c, 1, 2), "BFS", l);
        T.expect("BFS must append to the list, not clear it", "[x, y, 1, 2]", () -> l.toString());
        List<Object> m = new ArrayList<>(Arrays.asList("x"));
        T.call(tree(c, 1, 2, 3), "inorder", m);
        T.expect("inorder must append to the list, not clear it", "[x, 2, 1, 3]", () -> m.toString());
    }

    static void remove(String c) {
        T.method(c, "remove", 1);
        Object t = tree(c, 1, 2, 3, 4, 5, 6);
        T.expect("remove(2) should return true", true, () -> T.call(t, "remove", 2));
        T.expect("2 is replaced by the last element 6: BFS should be [1, 6, 3, 4, 5]", "[1, 6, 3, 4, 5]", () -> trav(t, "BFS"));
        T.expect("inorder should then be [4, 6, 5, 1, 3]", "[4, 6, 5, 1, 3]", () -> trav(t, "inorder"));
        T.expect("size() should be 5", 5, () -> T.call(t, "size"));
        T.expect("remove(5) (the last element) should return true", true, () -> T.call(t, "remove", 5));
        T.expect("BFS should be [1, 6, 3, 4]", "[1, 6, 3, 4]", () -> trav(t, "BFS"));
        T.expect("remove(1) (the root) should return true", true, () -> T.call(t, "remove", 1));
        T.expect("the root is replaced by 4: BFS should be [4, 6, 3]", "[4, 6, 3]", () -> trav(t, "BFS"));
        T.expect("remove(9) should return false", false, () -> T.call(t, "remove", 9));
        T.expect("remove(null) should return false", false, () -> T.call(t, "remove", (Object) null));
        T.call(t, "insert", 8);
        T.expect("insert after removes should fill the next position: BFS [4, 6, 3, 8]", "[4, 6, 3, 8]", () -> trav(t, "BFS"));
        Object d = tree(c, 5, 7, 5, 9);
        T.call(d, "remove", 5);
        T.expect("with duplicates only the first 5 (level order) is removed: BFS [9, 7, 5]", "[9, 7, 5]", () -> trav(d, "BFS"));
        Object one = tree(c, "z");
        T.expect("removing the only element should return true", true, () -> T.call(one, "remove", "z"));
        T.expectTrue("the tree must be empty after removing its only element", () -> T.call(one, "isEmpty"));
        T.call(one, "insert", "w");
        T.expect("inserting again into the emptied tree should give [w]", "[w]", () -> trav(one, "BFS"));
    }

    static void contains(String c) {
        T.method(c, "contains", 1);
        Object t = tree(c, "a", "b", "c", "d");
        T.expect("contains(a) (the root) should be true", true, () -> T.call(t, "contains", "a"));
        T.expect("contains(d) (a leaf) should be true", true, () -> T.call(t, "contains", "d"));
        T.expect("contains must use equals: contains(new String(\"c\")) should be true", true,
                () -> T.call(t, "contains", new String("c")));
        T.expect("contains(z) should be false", false, () -> T.call(t, "contains", "z"));
        T.expect("contains(null) should be false", false, () -> T.call(t, "contains", (Object) null));
        T.call(t, "insert", (Object) null);
        T.expect("insert(null) must be ignored: size() should stay 4", 4, () -> T.call(t, "size"));
    }

    static void empty(String c) {
        Object t = tree(c);
        T.expectTrue("a new tree must be empty", () -> T.call(t, "isEmpty"));
        T.expect("a new tree should have size 0", 0, () -> T.call(t, "size"));
        T.expect("BFS of an empty tree should add nothing", "[]", () -> trav(t, "BFS"));
        T.expect("DFS of an empty tree should add nothing", "[]", () -> trav(t, "DFS"));
        T.expect("preorder of an empty tree should add nothing", "[]", () -> trav(t, "preorder"));
        T.expect("inorder of an empty tree should add nothing", "[]", () -> trav(t, "inorder"));
        T.expect("postorder of an empty tree should add nothing", "[]", () -> trav(t, "postorder"));
        T.expect("remove on an empty tree should return false", false, () -> T.call(t, "remove", 1));
        T.expect("contains on an empty tree should return false", false, () -> T.call(t, "contains", 1));
        T.call(t, "insert", 3);
        T.call(t, "insert", 4);
        T.expect("size() should be 2 after two inserts", 2, () -> T.call(t, "size"));
        T.expectFalse("isEmpty() should be false after inserts", (boolean) T.call(t, "isEmpty"));
    }

    public static void main(String[] args) {

        T.test("ArrayBT", "test_ArrayBT_insert", "ArrayBT insert and array layout", () -> {
            T.method("ArrayBT", "insert", 1);
            Object t = T.make("ArrayBT", 4);
            T.call(t, "insert", "a");
            T.call(t, "insert", "b");
            T.call(t, "insert", "c");
            T.expect("after insert a, b, c the array should be [a, b, c, null]", "[a, b, c, null]",
                    () -> Arrays.toString((Object[]) T.field(t, "data")));
            T.call(t, "insert", "d");
            T.call(t, "insert", "e");
            T.expect("inserting into a full ArrayBT must be ignored: [a, b, c, d]", "[a, b, c, d]",
                    () -> Arrays.toString((Object[]) T.field(t, "data")));
            T.expect("size() should be 4", 4, () -> T.call(t, "size"));
            Object u = tree("ArrayBT", 1, 2, 3, 4, 5, 6);
            T.call(u, "remove", 2);
            T.expect("remove must null out the last slot: data starts [1, 6, 3, 4, 5, null]", "[1, 6, 3, 4, 5, null]",
                    () -> Arrays.toString(Arrays.copyOf((Object[]) T.field(u, "data"), 6)));
        });
        T.test("ArrayBT", "test_ArrayBT_traversals", "ArrayBT preorder, inorder, postorder", () -> traversals("ArrayBT"));
        T.test("ArrayBT", "test_ArrayBT_bfsDfs", "ArrayBT BFS and DFS", () -> bfsDfs("ArrayBT"));
        T.test("ArrayBT", "test_ArrayBT_remove", "ArrayBT remove", () -> remove("ArrayBT"));
        T.test("ArrayBT", "test_ArrayBT_contains", "ArrayBT contains and null", () -> contains("ArrayBT"));
        T.test("ArrayBT", "test_ArrayBT_empty", "ArrayBT empty tree", () -> empty("ArrayBT"));

        T.test("LinkedBT", "test_LinkedBT_insert", "LinkedBT insert keeps the tree complete", () -> {
            T.method("LinkedBT", "insert", 1);
            Object t = tree("LinkedBT", 1);
            T.expect("after insert(1) the root should hold 1", "1", () -> shape(T.field(t, "root")));
            T.call(t, "insert", 2);
            T.expect("insert(2) should become the left child: 1(2,-)", "1(2,-)", () -> shape(T.field(t, "root")));
            for (int i = 3; i <= 6; i++) {
                T.call(t, "insert", i);
            }
            T.expect("after inserting 1..6 the shape should be 1(2(4,5),3(6,-))", "1(2(4,5),3(6,-))",
                    () -> shape(T.field(t, "root")));
            T.expect("size() should be 6", 6, () -> T.call(t, "size"));
            T.call(t, "remove", 3);
            T.expect("remove(3) should give 1(2(4,5),6) (6 moved up, its old node detached)", "1(2(4,5),6)",
                    () -> shape(T.field(t, "root")));
            T.call(t, "remove", 1);
            T.expect("remove(1) should give 5(2(4,-),6)", "5(2(4,-),6)", () -> shape(T.field(t, "root")));
        });
        T.test("LinkedBT", "test_LinkedBT_traversals", "LinkedBT preorder, inorder, postorder", () -> traversals("LinkedBT"));
        T.test("LinkedBT", "test_LinkedBT_bfsDfs", "LinkedBT BFS and DFS", () -> bfsDfs("LinkedBT"));
        T.test("LinkedBT", "test_LinkedBT_remove", "LinkedBT remove", () -> remove("LinkedBT"));
        T.test("LinkedBT", "test_LinkedBT_contains", "LinkedBT contains and null", () -> contains("LinkedBT"));
        T.test("LinkedBT", "test_LinkedBT_empty", "LinkedBT empty tree", () -> {
            empty("LinkedBT");
            Object t = tree("LinkedBT", 1);
            T.call(t, "remove", 1);
            T.expectNull("root must be null after removing the only node", () -> T.field(t, "root"));
        });

        T.test("(both)", "test_Lab07_sameShape", "ArrayBT and LinkedBT agree", () -> {
            java.util.Random rnd = new java.util.Random(201);
            Object a = tree("ArrayBT");
            Object b = tree("LinkedBT");
            boolean same = true;
            String where = "";
            for (int step = 0; step < 60 && same; step++) {
                int v = rnd.nextInt(10);
                String op;
                if (rnd.nextInt(3) == 0) {
                    op = "remove(" + v + ")";
                    T.call(a, "remove", v);
                    T.call(b, "remove", v);
                } else {
                    op = "insert(" + v + ")";
                    T.call(a, "insert", v);
                    T.call(b, "insert", v);
                }
                for (String how : new String[] {"BFS", "inorder", "postorder", "DFS"}) {
                    if (!trav(a, how).equals(trav(b, how))) {
                        same = false;
                        where = " (they differ in " + how + " after " + op + ")";
                        break;
                    }
                }
            }
            T.expectTrue("60 random inserts/removes must give the same traversals for ArrayBT and LinkedBT" + where, same);
        });

        T.test("(code rules)", "test_Lab07_rules", "Lab07 code rules", () -> {
            T.expectTrue("ArrayBT and LinkedBT must implement ITree", () -> T.isA("ArrayBT", "ITree") && T.isA("LinkedBT", "ITree"));
            T.expectTrue("all fields of ArrayBT and LinkedBT must be private",
                    () -> T.fieldsPrivate("ArrayBT") && T.fieldsPrivate("LinkedBT"));
            T.expectTrue("LinkedBT.DFS must use a Stack", T.matches(F, "new\\s+Stack\\s*<"));
            T.expectTrue("LinkedBT.BFS must use a Queue", T.matches(F, "Queue\\s*<\\s*Node\\s*<"));
            T.expectFalse("no method may throw an exception (remove every \"Not implemented\" stub)", T.matches(F, "\\bthrow\\s+new\\b"));
            T.expectSilent("no method except main may print", () -> {
                for (String c : new String[] {"ArrayBT", "LinkedBT"}) {
                    try {
                        Object t = tree(c, 1, 2, 3);
                        trav(t, "BFS");
                        trav(t, "DFS");
                        trav(t, "inorder");
                        T.call(t, "remove", 2);
                        T.call(t, "remove", 9);
                        T.call(t, "contains", 3);
                    } catch (Throwable ignored) {
                        // printing is what matters here
                    }
                }
            });
        });

        T.done();
    }
}
