import java.util.*;

public class Lab12 {

    public static void main(String[] args) {
        // You may test your graph here manually.
        // Official testing will be done with JUnit.
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
        if (neighbor == null || neighbor == this || neighbors.contains(neighbor)) return;
        neighbors.add(neighbor);
    }

    @Override
    public void removeNeighbor(Node<T> neighbor) {
        neighbors.remove(neighbor);
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
        if (data == null || nodes.containsKey(data)) return;
        nodes.put(data, new Node<>(data));
    }

    @Override
    public void addEdge(T data1, T data2) {
        if (data1 == null || data2 == null || data1.equals(data2)) return;
        addNode(data1);
        addNode(data2);
        Node<T> a = nodes.get(data1);
        Node<T> b = nodes.get(data2);
        a.addNeighbor(b);
        b.addNeighbor(a);
    }

    @Override
    public void removeEdge(T data1, T data2) {
        Node<T> a = nodes.get(data1);
        Node<T> b = nodes.get(data2);
        if (a == null || b == null) return;
        a.removeNeighbor(b);
        b.removeNeighbor(a);
    }

    @Override
    public void removeNode(T data) {
        Node<T> n = nodes.get(data);
        if (n == null) return;
        for (Node<T> other : nodes.values()) other.removeNeighbor(n);
        nodes.remove(data);
    }

    @Override
    public List<T> bfs(T startData) {
        List<T> result = new ArrayList<>();
        Node<T> start = nodes.get(startData);
        if (start == null) return result;
        Set<Node<T>> visited = new HashSet<>();
        Queue<Node<T>> q = new LinkedList<>();
        q.add(start);
        visited.add(start);
        while (!q.isEmpty()) {
            Node<T> c = q.poll();
            result.add(c.data);
            for (Node<T> nb : c.neighbors) {
                if (visited.add(nb)) q.add(nb);
            }
        }
        return result;
    }

    @Override
    public List<T> dfs(T startData) {
        List<T> result = new ArrayList<>();
        Node<T> start = nodes.get(startData);
        if (start == null) return result;
        dfsHelper(start, new HashSet<>(), result);
        return result;
    }

    private void dfsHelper(Node<T> current, Set<Node<T>> visited, List<T> result) {
        visited.add(current);
        result.add(current.data);
        for (Node<T> nb : current.neighbors) {
            if (!visited.contains(nb)) dfsHelper(nb, visited, result);
        }
    }


    @Override
    public List<T> getShortestPath(T startData, T endData) {
        List<T> path = new ArrayList<>();
        Node<T> start = nodes.get(startData);
        Node<T> end = nodes.get(endData);
        if (start == null || end == null) return path;
        for (Node<T> n : nodes.values()) {   // reset the BFS helper fields
            n.visited = false;
            n.parent = null;
        }
        Queue<Node<T>> q = new LinkedList<>();
        start.visited = true;
        q.add(start);
        while (!q.isEmpty()) {
            Node<T> c = q.poll();
            if (c == end) break;
            for (Node<T> nb : c.neighbors) {
                if (!nb.visited) {
                    nb.visited = true;
                    nb.parent = c;
                    q.add(nb);
                }
            }
        }
        if (!end.visited) return path;   // unreachable
        for (Node<T> c = end; c != null; c = c.parent) path.add(0, c.data);
        return path;
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
