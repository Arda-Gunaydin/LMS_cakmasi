/** Hidden tests for last year's Lab 11: Red-Black Trees (school style: one test = one check). */
public class Tests {

    static final String F = "Lab11.java";

    static Object tree(Object... items) {
        Object t = T.make("RBTree");
        for (Object x : items) {
            T.call(t, "insert", x);
        }
        return t;
    }

    static Object root(Object t) {
        return T.field(t, "root");
    }

    /** value+color(left,right), e.g. 20B(10R,50R). */
    static String rb(Object n) {
        if (n == null) {
            return "-";
        }
        Object l = T.field(n, "left");
        Object r = T.field(n, "right");
        String d = T.field(n, "data") + ((Boolean) T.field(n, "color") ? "R" : "B");
        return l == null && r == null ? d : d + "(" + rb(l) + "," + rb(r) + ")";
    }

    /**
     * Checks BST order, parent links, no red-red and equal black heights.
     * Returns the black height; fails with a description.
     */
    static int valid(Object n, Object parent, Integer lo, Integer hi, int[] count) {
        if (n == null) {
            return 1;
        }
        count[0]++;
        int v = (Integer) T.field(n, "data");
        if (T.field(n, "parent") != parent) {
            throw T.failWith("the parent reference of node " + v + " is wrong");
        }
        if ((lo != null && v <= lo) || (hi != null && v >= hi)) {
            throw T.failWith("the BST order is broken at node " + v);
        }
        boolean red = (Boolean) T.field(n, "color");
        Object l = T.field(n, "left");
        Object r = T.field(n, "right");
        if (red && ((l != null && (Boolean) T.field(l, "color")) || (r != null && (Boolean) T.field(r, "color")))) {
            throw T.failWith("red node " + v + " has a red child");
        }
        int bl = valid(l, n, lo, v, count);
        int br = valid(r, n, v, hi, count);
        if (bl != br) {
            throw T.failWith("the black heights below node " + v + " differ (" + bl + " vs " + br + ")");
        }
        return bl + (red ? 0 : 1);
    }

    static String validTree(Object t) {
        Object r = root(t);
        if (r == null) {
            return "ok";
        }
        if ((Boolean) T.field(r, "color")) {
            throw T.failWith("the root must be black");
        }
        int[] count = {0};
        valid(r, null, null, null, count);
        if (count[0] != (Integer) T.call(t, "size")) {
            throw T.failWith("size() is " + T.call(t, "size") + " but the tree has " + count[0] + " nodes");
        }
        return "ok";
    }

    static int height(Object n) {
        return n == null ? 0 : 1 + Math.max(height(T.field(n, "left")), height(T.field(n, "right")));
    }

    public static void main(String[] args) {

        T.test("RBTree", "test_RBTree_empty", "RBTree empty, size and first insert", () -> {
            Object t = T.make("RBTree");
            T.expectTrue("a new tree must be empty", () -> T.call(t, "isEmpty"));
            T.expect("size() of a new tree should be 0", 0, () -> T.call(t, "size"));
            T.expect("contains(1) on an empty tree should be false", false, () -> T.call(t, "contains", 1));
            T.call(t, "insert", 10);
            T.expect("the first insert makes a black root: 10B", "10B", () -> rb(root(t)));
            T.expect("size() should be 1", 1, () -> T.call(t, "size"));
            T.expectFalse("isEmpty() should be false", (boolean) T.call(t, "isEmpty"));
            T.expectNull("the root's parent must be null", () -> T.field(root(t), "parent"));
        });

        T.test("RBTree", "test_RBTree_redChildren", "new nodes are red", () -> {
            T.method("RBTree", "insert", 1);
            Object t = tree(20, 10, 30);
            T.expect("insert 20, 10, 30 should give 20B(10R,30R)", "20B(10R,30R)", () -> rb(root(t)));
            T.expect("size() should be 3", 3, () -> T.call(t, "size"));
            T.expectSame("the parent of 10 must be the root", root(t), () -> T.field(T.field(root(t), "left"), "parent"));
        });

        T.test("RBTree", "test_RBTree_recolor", "red uncle: recolor", () -> {
            T.method("RBTree", "recolor", 1);
            Object t = tree(20, 10, 30, 5);
            T.expect("insert 20, 10, 30, 5 (red uncle) should give 20B(10B(5R,-),30B)", "20B(10B(5R,-),30B)", () -> rb(root(t)));
            Object u = tree(20, 10, 30);
            T.call(u, "recolor", root(u));
            T.expect("recolor(root) keeps the root black and makes both children black: 20B(10B,30B)", "20B(10B,30B)",
                    () -> rb(root(u)));
            Object w = tree(20, 10, 30, 5, 15, 25, 35, 1);
            T.expect("insert 20, 10, 30, 5, 15, 25, 35, 1 should give 20B(10R(5B(1R,-),15B),30B(25R,35R))",
                    "20B(10R(5B(1R,-),15B),30B(25R,35R))", () -> rb(root(w)));
        });

        T.test("RBTree", "test_RBTree_singleRotations", "black uncle: LL and RR", () -> {
            T.method("RBTree", "balance", 1);
            Object ll = tree(50, 20, 10);
            T.expect("insert 50, 20, 10 (LL) should give 20B(10R,50R)", "20B(10R,50R)", () -> rb(root(ll)));
            T.expectNull("after a rotation at the root, the new root's parent must be null", () -> T.field(root(ll), "parent"));
            Object rr = tree(10, 20, 30);
            T.expect("insert 10, 20, 30 (RR) should give 20B(10R,30R)", "20B(10R,30R)", () -> rb(root(rr)));
            Object deep = tree(20, 10, 30, 40, 50);
            T.expect("insert 20, 10, 30, 40, 50 (RR below the root) should give 20B(10B,40B(30R,50R))", "20B(10B,40B(30R,50R))",
                    () -> rb(root(deep)));
        });

        T.test("RBTree", "test_RBTree_doubleRotations", "black uncle: LR and RL", () -> {
            Object lr = tree(50, 10, 20);
            T.expect("insert 50, 10, 20 (LR) should give 20B(10R,50R)", "20B(10R,50R)", () -> rb(root(lr)));
            Object rl = tree(10, 50, 20);
            T.expect("insert 10, 50, 20 (RL) should give 20B(10R,50R)", "20B(10R,50R)", () -> rb(root(rl)));
            Object deep = tree(20, 10, 30, 40, 35);
            T.expect("insert 20, 10, 30, 40, 35 (RL below the root) should give 20B(10B,35B(30R,40R))", "20B(10B,35B(30R,40R))",
                    () -> rb(root(deep)));
            T.expect("the tree must satisfy all red-black properties", "ok", () -> validTree(deep));
        });

        T.test("RBTree", "test_RBTree_rotateLeft", "rotateLeft", () -> {
            T.method("RBTree", "rotateLeft", 1);
            Object t = tree(20, 10, 30, 25, 35);
            Object r = root(t);
            Object x = T.field(r, "right");
            Object ret = T.call(t, "rotateLeft", r);
            T.expectSame("rotateLeft(root) should return the old right child", x, () -> ret);
            T.expectSame("the root field must now be the old right child", x, () -> root(t));
            T.expect("rotateLeft(root) of 20B(10B,30B(25R,35R)) should give 30B(20B(10B,25R),35R) (colors unchanged)",
                    "30B(20B(10B,25R),35R)", () -> rb(root(t)));
            T.expectNull("the new root's parent must be null", () -> T.field(root(t), "parent"));
            T.expectSame("the old root's parent must be the new root", x, () -> T.field(r, "parent"));
            T.expectSame("25 moved under 20: its parent must be 20", r, () -> T.field(T.field(r, "right"), "parent"));
            Object leaf = T.field(root(t), "right");
            T.expectSame("rotateLeft on a node without a right child should return that node", leaf,
                    () -> T.call(t, "rotateLeft", leaf));
        });

        T.test("RBTree", "test_RBTree_rotateRight", "rotateRight", () -> {
            T.method("RBTree", "rotateRight", 1);
            Object t = tree(20, 10, 30, 5, 15, 25, 35, 1);
            Object ten = T.field(root(t), "left");
            Object five = T.field(ten, "left");
            Object ret = T.call(t, "rotateRight", ten);
            T.expectSame("rotateRight(10) should return 5", five, () -> ret);
            T.expectSame("5 must become the root's left child", five, () -> T.field(root(t), "left"));
            T.expectSame("5's parent must be the root", root(t), () -> T.field(five, "parent"));
            T.expect("the tree should be 20B(5B(1R,10R(-,15B)),30B(25R,35R))", "20B(5B(1R,10R(-,15B)),30B(25R,35R))",
                    () -> rb(root(t)));
            T.expectSame("10's parent must be 5", five, () -> T.field(ten, "parent"));
        });

        T.test("RBTree", "test_RBTree_lastYear", "last year's example sequences", () -> {
            T.expect("insert 33, 13, 53, 11, 21, 41, 61, 15, 31", "33B(13R(11B,21B(15R,31R)),53B(41R,61R))",
                    () -> rb(root(tree(33, 13, 53, 11, 21, 41, 61, 15, 31))));
            T.expect("insert 36, 15, 50, 70, 5, 30, 3, 6, 23, 33, 32",
                    "30B(15R(5B(3R,6R),23B),36R(33B(32R,-),50B(-,70R)))",
                    () -> rb(root(tree(36, 15, 50, 70, 5, 30, 3, 6, 23, 33, 32))));
            T.expect("insert 1..8", "4B(2R(1B,3B),6R(5B,7B(-,8R)))", () -> rb(root(tree(1, 2, 3, 4, 5, 6, 7, 8))));
        });

        T.test("RBTree", "test_RBTree_duplicates", "duplicates, null and contains", () -> {
            Object t = tree(20, 10, 30, 5);
            T.call(t, "insert", 10);
            T.call(t, "insert", 5);
            T.call(t, "insert", (Object) null);
            T.expect("duplicates and null must be ignored: size() stays 4", 4, () -> T.call(t, "size"));
            T.expect("and the tree must not change: 20B(10B(5R,-),30B)", "20B(10B(5R,-),30B)", () -> rb(root(t)));
            T.expect("contains(5) should be true", true, () -> T.call(t, "contains", 5));
            T.expect("contains(30) should be true", true, () -> T.call(t, "contains", 30));
            T.expect("contains(7) should be false", false, () -> T.call(t, "contains", 7));
            T.expect("contains(null) should be false", false, () -> T.call(t, "contains", (Object) null));
        });

        T.test("RBTree", "test_RBTree_sorted", "sorted input stays balanced", () -> {
            Object t = T.make("RBTree");
            for (int i = 1; i <= 1000; i++) {
                T.call(t, "insert", i);
            }
            T.expect("size() should be 1000", 1000, () -> T.call(t, "size"));
            T.expect("after inserting 1..1000 the tree must satisfy all red-black properties", "ok", () -> validTree(t));
            T.expectTrue("the height after inserting 1..1000 must be at most 2 log2(1001), about 19",
                    () -> height(root(t)) <= 19);
        });

        T.test("RBTree", "test_RBTree_random", "random inserts", () -> {
            java.util.Random rnd = new java.util.Random(11);
            java.util.TreeSet<Integer> ref = new java.util.TreeSet<>();
            Object t = T.make("RBTree");
            String bad = null;
            for (int i = 0; i < 400 && bad == null; i++) {
                int v = rnd.nextInt(600);
                ref.add(v);
                T.call(t, "insert", v);
                try {
                    validTree(t);
                } catch (Throwable e) {
                    bad = "after inserting " + v + ": " + e.getMessage();
                }
            }
            T.expectTrue("the red-black properties must hold after every insert" + (bad == null ? "" : " (" + bad + ")"), bad == null);
            T.expect("size() should equal the number of distinct inserted values", ref.size(), () -> T.call(t, "size"));
            T.expectTrue("contains must find every inserted value", () -> {
                for (int v : ref) {
                    if (!Boolean.TRUE.equals(T.call(t, "contains", v))) {
                        return false;
                    }
                }
                return true;
            });
        });

        T.test("(code rules)", "test_Lab11_rules", "Lab11 code rules", () -> {
            T.expectTrue("RBTree must implement IRBTree", () -> T.isA("RBTree", "IRBTree"));
            T.expectFalse("no \"Not implemented\" stub may be left (no throw new ...)", T.matches(F, "\\bthrow\\s+new\\b"));
            T.expectSilent("no method except main may print", () -> {
                try {
                    Object t = tree(5, 3, 8, 1, 4, 7, 9, 2, 6);
                    T.call(t, "contains", 4);
                    T.call(t, "insert", 4);
                } catch (Throwable ignored) {
                    // printing is what matters here
                }
            });
        });

        T.done();
    }
}
