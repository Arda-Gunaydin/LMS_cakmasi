/**
 * CSE201 Lab 12 - Adjacency List Graph (last year's lab, 2025-2026).
 * Read the description before you start. Only Lab12 is public. No package declaration.
 */
import java.util.*;

public class Lab12 {

    public static void main(String[] args) {
        // You may test your graph here manually.
        try {
            Graph<String> g = new Graph<>();
            g.addEdge("A", "B");
            g.addEdge("A", "C");
            g.addEdge("B", "D");
            g.addEdge("C", "D");
            g.addEdge("D", "E");
            g.printGraph();
            System.out.println("bfs(A)             = " + g.bfs("A"));                  // [A, B, C, D, E]
            System.out.println("dfs(A)             = " + g.dfs("A"));                  // [A, B, D, C, E]
            System.out.println("shortest path A->E = " + g.getShortestPath("A", "E")); // [A, B, D, E]
        } catch (UnsupportedOperationException e) {
            System.out.println();
            System.out.println("Stopped at a method that is not implemented yet:");
            System.out.println("  " + e.getStackTrace()[0]);
        }
    }
}

interface IUndirectedNode<T> {
    void addNeighbor(Node<T> neighbor);
    void removeNeighbor(Node<T> neighbor);
}


class Node<T> implements IUndirectedNode<T> {
    T data;
    List<Node<T>> neighbors;

    // BFS helper fields for easier implementation for shortest path method
    boolean visited;
    Node<T> parent;

    public Node(T data) {
        this.data = data;
        this.neighbors = new ArrayList<>();
    }

    @Override
    public void addNeighbor(Node<T> neighbor) {
        // TODO: add given node to neighbor list
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void removeNeighbor(Node<T> neighbor) {
        // TODO: remove given node from neighbor list
        throw new UnsupportedOperationException("Not implemented");
    }


    @Override
    public String toString() {
        return data.toString();
    }
}


interface AdjacencyList<T> {
    void addNode(T node);
    void addEdge(T data1, T data2);
    void removeNode(T data);
    void removeEdge(T data1, T data2);
    List<T> bfs(T startData);
    List<T> dfs(T startData);
    List<T> getShortestPath(T startData, T endData);
}

class Graph<T> implements AdjacencyList<T> {
    private Map<T, Node<T>> nodes;

    public Graph() {
        this.nodes = new HashMap<>();
    }


    @Override
    public void addNode(T data) {
        /*
         * TODO:
         * Add a new node if it does not already exist.
         * Store it inside nodes.
         */
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void addEdge(T data1, T data2) {
        /*
         * TODO:
         * Ensure both nodes exist (create if needed).
         * Add each node as a neighbor to the other (undirected graph).
         */
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void removeEdge(T data1, T data2) {
        /*
         * TODO:
         * If both nodes exist:
         * - Remove data2 from data1's neighbor list
         * - Remove data1 from data2's neighbor list
         */
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void removeNode(T data) {
        /*
         * TODO:
         * If node exists:
         * - Remove this node from all neighbor lists
         * - Remove the node from nodes
         */
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public List<T> bfs(T startData) {
        /*
         * TODO:
         * Perform Breadth-First Search starting from startData.
         *
         * Steps:
         * 1. Get starting Node
         * 2. Use a Queue for BFS
         * 3. Maintain a visited Set
         * 4. Add nodes to result in order visited
         */
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public List<T> dfs(T startData) {
        /*
         * TODO:
         * Perform Depth-First Search (recursive or stack version).
         *
         * Steps:
         * 1. Use a Set<Node> to track visited nodes
         * 2. Visit node, then recursively visit neighbors
         */
        throw new UnsupportedOperationException("Not implemented");
    }

    private void dfsHelper(Node<T> current, Set<Node<T>> visited, List<T> result) {
        /*
         * TODO:
         * Recursive DFS helper.
         */
        throw new UnsupportedOperationException("Not implemented");
    }


    @Override
    public List<T> getShortestPath(T startData, T endData) {
        /*
         * TODO:
         * Use BFS to compute shortest path in an unweighted graph.
         *
         * Steps:
         * 1. BFS until reaching end node
         * 2. Reconstruct path by following parent pointers
         * 3. Reverse and return the path
         */
        throw new UnsupportedOperationException("Not implemented");
    }

    public void printGraph() {
        for (T key : nodes.keySet()) {
            System.out.print(key + " -> ");
            Node<T> node = nodes.get(key);
            for (Node<T> neighbor : node.neighbors) {
                System.out.print(neighbor.data + " ");
            }
            System.out.println();
        }
    }
}
