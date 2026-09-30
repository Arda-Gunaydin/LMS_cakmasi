import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Hidden tests for last year's Lab 12: Adjacency List Graph (school style: one test = one check). */
public class Tests {

    static final String F = "Lab12.java";

    /** A graph from edges given as "A-B" strings; a single name adds a node. */
    static Object graph(String... edges) {
        Object g = T.make("Graph");
        for (String e : edges) {
            String[] p = e.split("-");
            if (p.length == 1) {
                T.call(g, "addNode", p[0]);
            } else {
                T.call(g, "addEdge", p[0], p[1]);
            }
        }
        return g;
    }

    static Object example() {
        return graph("A-B", "A-C", "B-D", "C-D", "D-E");
    }

    static Map<?, ?> nodes(Object g) {
        return (Map<?, ?>) T.field(g, "nodes");
    }

    /** The neighbors of a value as a list of values, or "(no node)". */
    static String nbrs(Object g, Object v) {
        Object n = nodes(g).get(v);
        if (n == null) {
            return "(no node)";
        }
        List<Object> out = new ArrayList<>();
        for (Object x : (List<?>) T.field(n, "neighbors")) {
            out.add(T.field(x, "data"));
        }
        return out.toString();
    }

    static String call(Object g, String m, Object... a) {
        return String.valueOf(T.call(g, m, a));
    }

    public static void main(String[] args) {

        T.test("Node", "test_Node_neighbors", "Node addNeighbor and removeNeighbor", () -> {
            T.method("Node", "addNeighbor", 1);
            Object a = T.make("Node", "A");
            Object b = T.make("Node", "B");
            Object c = T.make("Node", "C");
            T.call(a, "addNeighbor", b);
            T.call(a, "addNeighbor", c);
            T.call(a, "addNeighbor", b);
            T.call(a, "addNeighbor", a);
            T.call(a, "addNeighbor", (Object) null);
            T.expect("after adding B, C, B again, itself and null, A's neighbors should be [B, C]", "[B, C]",
                    () -> String.valueOf(T.field(a, "neighbors")));
            T.call(a, "removeNeighbor", b);
            T.expect("after removeNeighbor(B) the neighbors should be [C]", "[C]", () -> String.valueOf(T.field(a, "neighbors")));
            T.expectNoThrow("removing a node that is not a neighbor must not throw", () -> T.call(a, "removeNeighbor", b));
            T.expect("addNeighbor must not add the reverse direction by itself: B has no neighbors", "[]",
                    () -> String.valueOf(T.field(b, "neighbors")));
        });

        T.test("Graph", "test_Graph_addNode", "addNode", () -> {
            T.method("Graph", "addNode", 1);
            Object g = graph("A", "B");
            T.expect("after addNode(A), addNode(B) the graph should have 2 nodes", 2, () -> nodes(g).size());
            T.expect("a new node has no neighbors", "[]", () -> nbrs(g, "A"));
            T.call(g, "addEdge", "A", "B");
            T.call(g, "addNode", "A");
            T.expect("addNode of an existing value must keep its edges", "[B]", () -> nbrs(g, "A"));
            T.expect("the graph should still have 2 nodes", 2, () -> nodes(g).size());
            T.call(g, "addNode", (Object) null);
            T.expect("addNode(null) must be ignored", 2, () -> nodes(g).size());
        });

        T.test("Graph", "test_Graph_addEdge", "addEdge", () -> {
            T.method("Graph", "addEdge", 2);
            Object g = example();
            T.expect("addEdge must create the missing nodes: 5 nodes", 5, () -> nodes(g).size());
            T.expect("neighbors of A should be [B, C] (insertion order)", "[B, C]", () -> nbrs(g, "A"));
            T.expect("neighbors of D should be [B, C, E]", "[B, C, E]", () -> nbrs(g, "D"));
            T.expect("edges are undirected: neighbors of E should be [D]", "[D]", () -> nbrs(g, "E"));
            T.call(g, "addEdge", "A", "B");
            T.call(g, "addEdge", "B", "A");
            T.expect("an existing edge must not be added twice: A's neighbors stay [B, C]", "[B, C]", () -> nbrs(g, "A"));
            T.expect("... and B's neighbors stay [A, D]", "[A, D]", () -> nbrs(g, "B"));
            T.call(g, "addEdge", "C", "C");
            T.expect("a self-loop must be ignored: C's neighbors stay [A, D]", "[A, D]", () -> nbrs(g, "C"));
            T.call(g, "addEdge", "A", null);
            T.expect("addEdge with null must be ignored", 5, () -> nodes(g).size());
            Object h = graph("X-Y");
            T.expect("addEdge(X, Y) on an empty graph: X -> [Y]", "[Y]", () -> nbrs(h, "X"));
        });

        T.test("Graph", "test_Graph_removeEdge", "removeEdge", () -> {
            T.method("Graph", "removeEdge", 2);
            Object g = example();
            T.call(g, "removeEdge", "D", "B");
            T.expect("removeEdge(D, B) must remove B from D's list: [C, E]", "[C, E]", () -> nbrs(g, "D"));
            T.expect("... and D from B's list: [A]", "[A]", () -> nbrs(g, "B"));
            T.expect("the nodes stay in the graph", 5, () -> nodes(g).size());
            T.expectNoThrow("removing a missing edge must not throw", () -> T.call(g, "removeEdge", "A", "E"));
            T.expectNoThrow("removing an edge with a missing node must not throw", () -> T.call(g, "removeEdge", "A", "Z"));
            T.expect("A's neighbors must be unchanged: [B, C]", "[B, C]", () -> nbrs(g, "A"));
        });

        T.test("Graph", "test_Graph_removeNode", "removeNode", () -> {
            T.method("Graph", "removeNode", 1);
            Object g = example();
            T.call(g, "removeNode", "D");
            T.expect("after removeNode(D) the graph should have 4 nodes", 4, () -> nodes(g).size());
            T.expect("D must be gone from B's list: [A]", "[A]", () -> nbrs(g, "B"));
            T.expect("D must be gone from C's list: [A]", "[A]", () -> nbrs(g, "C"));
            T.expect("D must be gone from E's list: []", "[]", () -> nbrs(g, "E"));
            T.expect("there must be no node for D", "(no node)", () -> nbrs(g, "D"));
            T.expectNoThrow("removing a missing node must not throw", () -> T.call(g, "removeNode", "Z"));
            T.expect("the graph should still have 4 nodes", 4, () -> nodes(g).size());
        });

        T.test("bfs", "test_bfs_order", "bfs", () -> {
            T.method("Graph", "bfs", 1);
            Object g = example();
            T.expect("bfs(A) should be [A, B, C, D, E]", "[A, B, C, D, E]", () -> call(g, "bfs", "A"));
            T.expect("bfs(E) should be [E, D, B, C, A]", "[E, D, B, C, A]", () -> call(g, "bfs", "E"));
            T.expect("bfs(C) should be [C, A, D, B, E]", "[C, A, D, B, E]", () -> call(g, "bfs", "C"));
            Object h = graph("1-2", "1-3", "1-4", "2-5", "3-5", "4-6", "5-6", "6-7");
            T.expect("bfs(1) should be [1, 2, 3, 4, 5, 6, 7]", "[1, 2, 3, 4, 5, 6, 7]", () -> call(h, "bfs", "1"));
            T.expect("bfs(7) should be [7, 6, 4, 5, 1, 2, 3]", "[7, 6, 4, 5, 1, 2, 3]", () -> call(h, "bfs", "7"));
        });

        T.test("bfs", "test_bfs_edge", "bfs on missing, single and disconnected", () -> {
            Object g = graph("A-B", "C-D", "E");
            T.expect("bfs(A) visits only A's component: [A, B]", "[A, B]", () -> call(g, "bfs", "A"));
            T.expect("bfs(E) on an isolated node should be [E]", "[E]", () -> call(g, "bfs", "E"));
            T.expect("bfs(Z) on a missing node should be []", "[]", () -> call(g, "bfs", "Z"));
            T.expect("bfs on an empty graph should be []", "[]", () -> call(T.make("Graph"), "bfs", "A"));
            Object cyc = graph("A-B", "B-C", "C-A");
            T.expect("a cycle must not repeat nodes: bfs(A) = [A, B, C]", "[A, B, C]", () -> call(cyc, "bfs", "A"));
        });

        T.test("dfs", "test_dfs_order", "dfs", () -> {
            T.method("Graph", "dfs", 1);
            Object g = example();
            T.expect("dfs(A) should be [A, B, D, C, E]", "[A, B, D, C, E]", () -> call(g, "dfs", "A"));
            T.expect("dfs(E) should be [E, D, B, A, C]", "[E, D, B, A, C]", () -> call(g, "dfs", "E"));
            Object h = graph("1-2", "1-3", "1-4", "2-5", "3-5", "4-6", "5-6", "6-7");
            T.expect("dfs(1) should be [1, 2, 5, 3, 6, 4, 7]", "[1, 2, 5, 3, 6, 4, 7]", () -> call(h, "dfs", "1"));
        });

        T.test("dfs", "test_dfs_edge", "dfs on missing, single and disconnected", () -> {
            Object g = graph("A-B", "C-D", "E");
            T.expect("dfs(C) visits only C's component: [C, D]", "[C, D]", () -> call(g, "dfs", "C"));
            T.expect("dfs(E) on an isolated node should be [E]", "[E]", () -> call(g, "dfs", "E"));
            T.expect("dfs(Z) on a missing node should be []", "[]", () -> call(g, "dfs", "Z"));
            Object cyc = graph("A-B", "B-C", "C-A");
            T.expect("a cycle must not repeat nodes: dfs(A) = [A, B, C]", "[A, B, C]", () -> call(cyc, "dfs", "A"));
            T.expect("calling dfs twice must give the same result", "[A, B, C]", () -> call(cyc, "dfs", "A"));
        });

        T.test("getShortestPath", "test_getShortestPath_basic", "getShortestPath", () -> {
            T.method("Graph", "getShortestPath", 2);
            Object g = example();
            T.expect("getShortestPath(A, E) should be [A, B, D, E]", "[A, B, D, E]", () -> call(g, "getShortestPath", "A", "E"));
            T.expect("getShortestPath(E, A) should be [E, D, B, A]", "[E, D, B, A]", () -> call(g, "getShortestPath", "E", "A"));
            T.expect("getShortestPath(C, B) should be [C, A, B]", "[C, A, B]", () -> call(g, "getShortestPath", "C", "B"));
            T.expect("getShortestPath(A, B) (neighbors) should be [A, B]", "[A, B]", () -> call(g, "getShortestPath", "A", "B"));
            Object h = graph("1-2", "2-3", "3-4", "4-5", "1-6", "6-5");
            T.expect("the shorter way round a cycle: getShortestPath(1, 5) should be [1, 6, 5]", "[1, 6, 5]",
                    () -> call(h, "getShortestPath", "1", "5"));
        });

        T.test("getShortestPath", "test_getShortestPath_edge", "getShortestPath edge cases", () -> {
            Object g = graph("A-B", "C-D");
            T.expect("getShortestPath(A, A) should be [A]", "[A]", () -> call(g, "getShortestPath", "A", "A"));
            T.expect("an unreachable end should give []", "[]", () -> call(g, "getShortestPath", "A", "D"));
            T.expect("a missing end should give []", "[]", () -> call(g, "getShortestPath", "A", "Z"));
            T.expect("a missing start should give []", "[]", () -> call(g, "getShortestPath", "Z", "A"));
            Object e = example();
            T.call(e, "removeNode", "D");
            T.expect("after removeNode(D), getShortestPath(A, E) should be []", "[]", () -> call(e, "getShortestPath", "A", "E"));
        });

        T.test("getShortestPath", "test_getShortestPath_repeated", "getShortestPath called many times", () -> {
            Object g = example();
            T.expect("first call: getShortestPath(A, E) = [A, B, D, E]", "[A, B, D, E]", () -> call(g, "getShortestPath", "A", "E"));
            T.expect("second call: getShortestPath(E, C) = [E, D, C]", "[E, D, C]", () -> call(g, "getShortestPath", "E", "C"));
            T.expect("third call: getShortestPath(B, C) = [B, A, C]", "[B, A, C]", () -> call(g, "getShortestPath", "B", "C"));
            T.expect("fourth call again: getShortestPath(A, E) = [A, B, D, E]", "[A, B, D, E]",
                    () -> call(g, "getShortestPath", "A", "E"));
            T.call(g, "addEdge", "A", "E");
            T.expect("after adding edge A-E: getShortestPath(A, E) = [A, E]", "[A, E]", () -> call(g, "getShortestPath", "A", "E"));
            Object big = T.make("Graph");
            for (int i = 0; i < 200; i++) {
                T.call(big, "addEdge", i, i + 1);
            }
            T.expect("a path along 200 edges should have 201 values", 201,
                    () -> ((List<?>) T.call(big, "getShortestPath", 0, 200)).size());
        });

        T.test("(code rules)", "test_Lab12_rules", "Lab12 code rules", () -> {
            T.expectTrue("Graph must implement AdjacencyList", () -> T.isA("Graph", "AdjacencyList"));
            T.expectTrue("Node must implement IUndirectedNode", () -> T.isA("Node", "IUndirectedNode"));
            T.expectFalse("no \"Not implemented\" stub may be left (no throw new ...)", T.matches(F, "\\bthrow\\s+new\\b"));
            T.expectSilent("no method except main and printGraph may print", () -> {
                try {
                    Object g = example();
                    T.call(g, "bfs", "A");
                    T.call(g, "dfs", "A");
                    T.call(g, "getShortestPath", "A", "E");
                    T.call(g, "removeEdge", "A", "B");
                    T.call(g, "removeNode", "D");
                } catch (Throwable ignored) {
                    // printing is what matters here
                }
            });
        });

        T.done();
    }
}
