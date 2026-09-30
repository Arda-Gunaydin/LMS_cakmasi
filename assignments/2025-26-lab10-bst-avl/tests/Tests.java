import java.lang.reflect.Field;

/** Hidden tests for last year's Lab 10: BST and AVL Trees (school style: one test = one check). */
public class Tests {

    static final String F = "Lab10.java";

    static Object tree(String cls, Object... items) {
        Object t = T.make(cls);
        for (Object x : items) {
            T.call(t, "insert", x);
        }
        return t;
    }

    static Object root(Object t) {
        return T.field(t, "root");
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

    /** value:height in preorder, e.g. "4:3 2:2 1:1". */
    static String heights(Object n) {
        if (n == null) {
            return "";
        }
        String s = T.field(n, "data") + ":" + T.field(n, "height");
        String l = heights(T.field(n, "left"));
        String r = heights(T.field(n, "right"));
        return (s + " " + l + " " + r).trim().replaceAll("\\s+", " ");
    }

    static void set(Object node, String field, Object value) throws Exception {
        Field f = T.fieldOf("Node", field);
        f.setAccessible(true);
        f.set(node, value);
    }

    /** A node with children and a given height. */
    static Object node(int v, Object left, Object right, int height) throws Exception {
        Object n = T.make("Node", v);
        set(n, "left", left);
        set(n, "right", right);
        set(n, "height", height);
        return n;
    }

    static Object leaf(int v) throws Exception {
        return node(v, null, null, 1);
    }

    /** Checks order, heights and balance of an AVL subtree; returns the node count, or throws a description. */
    static int check(Object n, Integer lo, Integer hi) {
        if (n == null) {
            return 0;
        }
        int v = (Integer) T.field(n, "data");
        if ((lo != null && v <= lo) || (hi != null && v >= hi)) {
            throw T.failWith("the BST order is broken at " + v);
        }
        Object l = T.field(n, "left");
        Object r = T.field(n, "right");
        int c = 1 + check(l, lo, v) + check(r, v, hi);
        int hl = l == null ? 0 : (Integer) T.field(l, "height");
        int hr = r == null ? 0 : (Integer) T.field(r, "height");
        if ((Integer) T.field(n, "height") != 1 + Math.max(hl, hr)) {
            throw T.failWith("the height field of node " + v + " is wrong");
        }
        if (Math.abs(hl - hr) > 1) {
            throw T.failWith("node " + v + " is not balanced (balance factor " + (hl - hr) + ")");
        }
        return c;
    }

    static int count(Object n) {
        return n == null ? 0 : 1 + count(T.field(n, "left")) + count(T.field(n, "right"));
    }

    public static void main(String[] args) {

        // ---------------------------------------------------------------- BST
        T.test("BST", "test_BST_insert", "BST insert", () -> {
            T.method("BST", "insertRec", 2);
            Object t = tree("BST", 50, 30, 70, 20, 40, 60, 80);
            T.expect("inserting 50, 30, 70, 20, 40, 60, 80 should give 50(30(20,40),70(60,80))", "50(30(20,40),70(60,80))",
                    () -> shape(root(t)));
            T.expect("size() should be 7", 7, () -> T.call(t, "size"));
            T.call(t, "insert", 40);
            T.call(t, "insert", (Object) null);
            T.expect("inserting a duplicate or null must change nothing: size() stays 7", 7, () -> T.call(t, "size"));
            T.expect("the shape must not change either", "50(30(20,40),70(60,80))", () -> shape(root(t)));
            Object chain = tree("BST", 1, 2, 3, 4);
            T.expect("a plain BST does not balance: 1, 2, 3, 4 gives the chain 1(-,2(-,3(-,4)))", "1(-,2(-,3(-,4)))",
                    () -> shape(root(chain)));
        });

        T.test("BST", "test_BST_contains", "BST contains and findMin", () -> {
            T.method("BST", "containsRec", 2);
            Object t = tree("BST", 50, 30, 70, 20, 40, 60, 80);
            T.expect("contains(50) (root) should be true", true, () -> T.call(t, "contains", 50));
            T.expect("contains(60) should be true", true, () -> T.call(t, "contains", 60));
            T.expect("contains(20) should be true", true, () -> T.call(t, "contains", 20));
            T.expect("contains(65) should be false", false, () -> T.call(t, "contains", 65));
            T.expect("contains(null) should be false", false, () -> T.call(t, "contains", (Object) null));
            T.expect("contains on an empty BST should be false", false, () -> T.call(T.make("BST"), "contains", 1));
            T.expect("findMin(root) should be the node 20", 20, () -> T.field(T.call(t, "findMin", root(t)), "data"));
            T.expect("findMin(root.right) should be the node 60", 60,
                    () -> T.field(T.call(t, "findMin", T.field(root(t), "right")), "data"));
            T.expectNull("findMin(null) should be null", () -> T.call(t, "findMin", (Object) null));
        });

        T.test("BST", "test_BST_removeSimple", "BST remove a leaf or a node with one child", () -> {
            T.method("BST", "removeRec", 2);
            Object t = tree("BST", 50, 30, 70, 20, 40, 60, 80, 65);
            T.call(t, "remove", 20);
            T.expect("remove(20) (a leaf): 50(30(-,40),70(60(-,65),80))", "50(30(-,40),70(60(-,65),80))", () -> shape(root(t)));
            T.call(t, "remove", 60);
            T.expect("remove(60) (only a right child): 50(30(-,40),70(65,80))", "50(30(-,40),70(65,80))", () -> shape(root(t)));
            T.call(t, "remove", 30);
            T.expect("remove(30) (only a right child): 50(40,70(65,80))", "50(40,70(65,80))", () -> shape(root(t)));
            T.expect("size() should be 5", 5, () -> T.call(t, "size"));
            T.call(t, "remove", 99);
            T.call(t, "remove", (Object) null);
            T.expect("removing a missing element or null must change nothing: size() stays 5", 5, () -> T.call(t, "size"));
            Object l = tree("BST", 5, 3, 1);
            T.call(l, "remove", 3);
            T.expect("remove(3) (only a left child) from 5(3(1,-),-): 5(1,-)", "5(1,-)", () -> shape(root(l)));
        });

        T.test("BST", "test_BST_removeTwoChildren", "BST remove a node with two children", () -> {
            Object t = tree("BST", 50, 30, 70, 60, 80);
            T.call(t, "remove", 50);
            T.expect("remove(50): the root takes its successor 60: 60(30,70(-,80))", "60(30,70(-,80))", () -> shape(root(t)));
            Object u = tree("BST", 50, 30, 70, 20, 40, 35, 45);
            T.call(u, "remove", 30);
            T.expect("remove(30): 30 takes its successor 35: 50(35(20,40(-,45)),70)", "50(35(20,40(-,45)),70)", () -> shape(root(u)));
            T.expect("size() should be 6", 6, () -> T.call(u, "size"));
            Object one = tree("BST", 9);
            T.call(one, "remove", 9);
            T.expectTrue("removing the only element should leave an empty tree", () -> T.call(one, "isEmpty"));
            T.expectNull("root should be null", () -> root(one));
            T.call(one, "insert", 4);
            T.expect("the emptied tree must work again", "4", () -> shape(root(one)));
        });

        // ---------------------------------------------------------------- AVL
        T.test("AVLTree", "test_AVLTree_heightAndBalanceFactor", "AVL height and getBalanceFactor", () -> {
            T.method("AVLTree", "getBalanceFactor", 1);
            Object t = T.make("AVLTree");
            T.expect("height() of an empty AVL tree should be 0", 0, () -> T.call(t, "height"));
            Object leftHeavy = node(30, node(20, leaf(10), null, 2), null, 3);
            T.expect("getBalanceFactor of 30(20(10,-),-) should be 2", 2, () -> T.call(t, "getBalanceFactor", leftHeavy));
            Object rightHeavy = node(10, null, leaf(20), 2);
            T.expect("getBalanceFactor of 10(-,20) should be -1", -1, () -> T.call(t, "getBalanceFactor", rightHeavy));
            T.expect("getBalanceFactor of a leaf should be 0", 0, () -> T.call(t, "getBalanceFactor", leaf(5)));
            T.expect("getBalanceFactor(null) should be 0", 0, () -> T.call(t, "getBalanceFactor", (Object) null));
            Object u = tree("AVLTree", 5);
            T.expect("height() of a one-node AVL tree should be 1", 1, () -> T.call(u, "height"));
        });

        T.test("AVLTree", "test_AVLTree_rotateLeft", "AVL rotateLeft", () -> {
            T.method("AVLTree", "rotateLeft", 1);
            Object t = T.make("AVLTree");
            Object y = node(10, leaf(5), node(20, leaf(15), leaf(25), 2), 3);
            Object r = T.call(t, "rotateLeft", y);
            T.expect("rotateLeft(10(5,20(15,25))) should give 20(10(5,15),25)", "20(10(5,15),25)", () -> shape(r));
            T.expect("the heights after rotateLeft should be 20:3 10:2 5:1 15:1 25:1", "20:3 10:2 5:1 15:1 25:1", () -> heights(r));
            Object simple = node(1, null, node(2, null, leaf(3), 2), 3);
            T.expect("rotateLeft(1(-,2(-,3))) should give 2(1,3)", "2(1,3)", () -> shape(T.call(t, "rotateLeft", simple)));
        });

        T.test("AVLTree", "test_AVLTree_rotateRight", "AVL rotateRight", () -> {
            T.method("AVLTree", "rotateRight", 1);
            Object t = T.make("AVLTree");
            Object x = node(20, node(10, leaf(5), leaf(15), 2), leaf(25), 3);
            Object r = T.call(t, "rotateRight", x);
            T.expect("rotateRight(20(10(5,15),25)) should give 10(5,20(15,25))", "10(5,20(15,25))", () -> shape(r));
            T.expect("the heights after rotateRight should be 10:3 5:1 20:2 15:1 25:1", "10:3 5:1 20:2 15:1 25:1", () -> heights(r));
        });

        T.test("AVLTree", "test_AVLTree_doubleRotations", "AVL rotateLeftRight, rotateRightLeft and balance", () -> {
            Object t = T.make("AVLTree");
            Object lr = node(30, node(10, null, leaf(20), 2), null, 3);
            Object r1 = T.call(t, "rotateLeftRight", lr);
            T.expect("rotateLeftRight(30(10(-,20),-)) should give 20(10,30)", "20(10,30)", () -> shape(r1));
            T.expect("heights should be 20:2 10:1 30:1", "20:2 10:1 30:1", () -> heights(r1));
            Object rl = node(10, null, node(30, leaf(20), null, 2), 3);
            Object r2 = T.call(t, "rotateRightLeft", rl);
            T.expect("rotateRightLeft(10(-,30(20,-))) should give 20(10,30)", "20(10,30)", () -> shape(r2));
            Object ll = node(30, node(20, leaf(10), null, 2), null, 3);
            T.expect("balance(30(20(10,-),-)) (LL) should give 20(10,30)", "20(10,30)", () -> shape(T.call(t, "balance", ll)));
            Object lrb = node(30, node(10, null, leaf(20), 2), null, 3);
            T.expect("balance(30(10(-,20),-)) (LR) should give 20(10,30)", "20(10,30)", () -> shape(T.call(t, "balance", lrb)));
            Object ok = node(20, leaf(10), null, 2);
            T.expectSame("balance of an already balanced node should return the same node", ok, () -> T.call(t, "balance", ok));
            T.expectNull("balance(null) should return null", () -> T.call(t, "balance", (Object) null));
        });

        T.test("AVLTree", "test_AVLTree_insertCases", "AVL insert: LL, RR, LR, RL", () -> {
            T.expect("insert 30, 20, 10 (LL) should give 20(10,30)", "20(10,30)", () -> shape(root(tree("AVLTree", 30, 20, 10))));
            T.expect("insert 10, 20, 30 (RR) should give 20(10,30)", "20(10,30)", () -> shape(root(tree("AVLTree", 10, 20, 30))));
            T.expect("insert 30, 10, 20 (LR) should give 20(10,30)", "20(10,30)", () -> shape(root(tree("AVLTree", 30, 10, 20))));
            T.expect("insert 10, 30, 20 (RL) should give 20(10,30)", "20(10,30)", () -> shape(root(tree("AVLTree", 10, 30, 20))));
            Object t = tree("AVLTree", 1, 2, 3, 4, 5, 6, 7);
            T.expect("insert 1..7 should give the perfect tree 4(2(1,3),6(5,7))", "4(2(1,3),6(5,7))", () -> shape(root(t)));
            T.expect("the heights should be 4:3 2:2 1:1 3:1 6:2 5:1 7:1", "4:3 2:2 1:1 3:1 6:2 5:1 7:1", () -> heights(root(t)));
            T.expect("height() should be 3", 3, () -> T.call(t, "height"));
            T.expect("size() should be 7", 7, () -> T.call(t, "size"));
            T.call(t, "insert", 4);
            T.expect("a duplicate must not change the size", 7, () -> T.call(t, "size"));
        });

        T.test("AVLTree", "test_AVLTree_remove", "AVL remove re-balances", () -> {
            Object a = tree("AVLTree", 20, 10, 30, 40);
            T.call(a, "remove", 10);
            T.expect("insert 20, 10, 30, 40 then remove 10 should give 30(20,40)", "30(20,40)", () -> shape(root(a)));
            Object b = tree("AVLTree", 20, 10, 30, 25, 35);
            T.call(b, "remove", 10);
            T.expect("insert 20, 10, 30, 25, 35 then remove 10 should give 30(20(-,25),35)", "30(20(-,25),35)", () -> shape(root(b)));
            T.expect("the heights should be 30:3 20:2 25:1 35:1", "30:3 20:2 25:1 35:1", () -> heights(root(b)));
            Object c = tree("AVLTree", 20, 10, 30, 25);
            T.call(c, "remove", 10);
            T.expect("insert 20, 10, 30, 25 then remove 10 (RL) should give 25(20,30)", "25(20,30)", () -> shape(root(c)));
            Object d = tree("AVLTree", 4, 2, 6, 1, 3, 5, 7);
            T.call(d, "remove", 4);
            T.expect("removing the root 4 of 4(2(1,3),6(5,7)) should give 5(2(1,3),6(-,7))", "5(2(1,3),6(-,7))", () -> shape(root(d)));
            T.expect("size() should be 6", 6, () -> T.call(d, "size"));
            T.call(d, "remove", 42);
            T.expect("removing a missing element must not change the size", 6, () -> T.call(d, "size"));
        });

        T.test("AVLTree", "test_AVLTree_sorted", "AVL stays logarithmic on sorted input", () -> {
            Object t = T.make("AVLTree");
            for (int i = 1; i <= 1000; i++) {
                T.call(t, "insert", i);
            }
            T.expect("after inserting 1..1000 size() should be 1000", 1000, () -> T.call(t, "size"));
            T.expect("after inserting 1..1000 in order, height() should be 10", 10, () -> T.call(t, "height"));
            T.expect("every node must be ordered, balanced and have the right height", 1000, () -> check(root(t), null, null));
            T.expect("contains(777) should be true", true, () -> T.call(t, "contains", 777));
        });

        T.test("AVLTree", "test_AVLTree_random", "AVL random inserts and removes", () -> {
            java.util.Random rnd = new java.util.Random(10);
            java.util.TreeSet<Integer> ref = new java.util.TreeSet<>();
            Object t = T.make("AVLTree");
            for (int step = 0; step < 600; step++) {
                int v = rnd.nextInt(120);
                if (rnd.nextInt(3) == 0) {
                    ref.remove(v);
                    T.call(t, "remove", v);
                } else {
                    ref.add(v);
                    T.call(t, "insert", v);
                }
            }
            T.expect("after 600 random operations size() should match", ref.size(), () -> T.call(t, "size"));
            T.expect("the tree must hold exactly the right number of nodes, ordered, balanced, with correct heights", ref.size(),
                    () -> check(root(t), null, null));
            T.expectTrue("contains must agree with the inserted/removed elements", () -> {
                for (int v = 0; v < 120; v++) {
                    if (!Boolean.valueOf(ref.contains(v)).equals(T.call(t, "contains", v))) {
                        return false;
                    }
                }
                return true;
            });
        });

        T.test("BST", "test_BST_random", "BST random inserts and removes", () -> {
            java.util.Random rnd = new java.util.Random(11);
            java.util.TreeSet<Integer> ref = new java.util.TreeSet<>();
            Object t = T.make("BST");
            for (int step = 0; step < 400; step++) {
                int v = rnd.nextInt(80);
                if (rnd.nextInt(3) == 0) {
                    ref.remove(v);
                    T.call(t, "remove", v);
                } else {
                    ref.add(v);
                    T.call(t, "insert", v);
                }
            }
            T.expect("after 400 random operations size() should match", ref.size(), () -> T.call(t, "size"));
            T.expect("the number of nodes should match size()", ref.size(), () -> count(root(t)));
            T.expectTrue("contains must agree with the inserted/removed elements", () -> {
                for (int v = 0; v < 80; v++) {
                    if (!Boolean.valueOf(ref.contains(v)).equals(T.call(t, "contains", v))) {
                        return false;
                    }
                }
                return true;
            });
        });

        T.test("(code rules)", "test_Lab10_rules", "Lab10 code rules", () -> {
            T.expectTrue("AVLTree must extend BST and implement IAVL",
                    () -> T.extendsDirectly("AVLTree", "BST") && T.isA("AVLTree", "IAVL"));
            T.expectFalse("Lab10.java must not use java.util", T.matches(F, "\\bjava\\s*\\.\\s*util\\b"));
            T.expectFalse("no \"Not implemented\" stub may be left (no throw new ...)", T.matches(F, "\\bthrow\\s+new\\b"));
            T.expectSilent("no method except main may print", () -> {
                for (String c : new String[] {"BST", "AVLTree"}) {
                    try {
                        Object t = tree(c, 3, 1, 2, 5, 4);
                        T.call(t, "contains", 4);
                        T.call(t, "remove", 3);
                        T.call(t, "remove", 9);
                    } catch (Throwable ignored) {
                        // printing is what matters here
                    }
                }
            });
        });

        T.done();
    }
}
