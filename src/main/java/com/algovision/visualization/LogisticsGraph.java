package com.algovision.visualization;

import java.util.*;

/**
 * MODULE 5: VISUALIZATION -- GRAPHS
 * --------------------------------------------------------------
 * DSA Concept: Weighted Graph (adjacency list), Dijkstra's Shortest Path,
 * BFS for connectivity/network overview.
 *
 * Real-world mapping: Warehouses/cities form a logistics network. We
 * model it as a weighted graph (edge weight = distance/cost/time) and
 * use Dijkstra to compute the cheapest delivery route from a central
 * warehouse to every destination -- exactly what routing engines
 * (e.g. Amazon's last-mile routing, Google Maps' core engine) do at scale.
 *
 * Complexity:
 *   addEdge          -> O(1)
 *   dijkstra         -> O((V + E) log V) using a PriorityQueue (min-heap)
 *   bfsTraversal     -> O(V + E)
 *   renderAdjacency  -> O(V + E)  (console visualization)
 */
public class LogisticsGraph {

    private final Map<Integer, String> nodeNames = new HashMap<>();
    private final Map<Integer, List<int[]>> adjacency = new HashMap<>(); // nodeId -> list of {neighborId, weight}

    public void addNode(int id, String name) {
        nodeNames.put(id, name);
        adjacency.putIfAbsent(id, new ArrayList<>());
    }

    /** Adds an undirected weighted edge (e.g. road/route between two warehouses/cities). */
    public void addEdge(int from, int to, int weight) {
        adjacency.get(from).add(new int[]{to, weight});
        adjacency.get(to).add(new int[]{from, weight});
    }

    public String nameOf(int id) {
        return nodeNames.getOrDefault(id, "Node" + id);
    }

    public Set<Integer> nodeIds() {
        return nodeNames.keySet();
    }

    /**
     * Dijkstra's algorithm: shortest distance from source to every other node.
     * Uses a PriorityQueue (binary min-heap) for O((V+E) log V).
     */
    public Map<Integer, Integer> dijkstra(int source) {
        Map<Integer, Integer> dist = new HashMap<>();
        for (int id : nodeNames.keySet()) dist.put(id, Integer.MAX_VALUE);
        dist.put(source, 0);

        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Integer.compare(a[1], b[1]));
        pq.offer(new int[]{source, 0});
        Set<Integer> visited = new HashSet<>();

        while (!pq.isEmpty()) {
            int[] curr = pq.poll();
            int u = curr[0];
            if (visited.contains(u)) continue;
            visited.add(u);

            for (int[] edge : adjacency.get(u)) {
                int v = edge[0], weight = edge[1];
                if (dist.get(u) + weight < dist.get(v)) {
                    dist.put(v, dist.get(u) + weight);
                    pq.offer(new int[]{v, dist.get(v)});
                }
            }
        }
        return dist;
    }

    /** Reconstructs the shortest path from source to target as a list of node IDs. */
    public List<Integer> shortestPath(int source, int target) {
        Map<Integer, Integer> dist = new HashMap<>();
        Map<Integer, Integer> prev = new HashMap<>();
        for (int id : nodeNames.keySet()) dist.put(id, Integer.MAX_VALUE);
        dist.put(source, 0);

        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Integer.compare(a[1], b[1]));
        pq.offer(new int[]{source, 0});
        Set<Integer> visited = new HashSet<>();

        while (!pq.isEmpty()) {
            int[] curr = pq.poll();
            int u = curr[0];
            if (visited.contains(u)) continue;
            visited.add(u);
            if (u == target) break;

            for (int[] edge : adjacency.get(u)) {
                int v = edge[0], weight = edge[1];
                if (dist.get(u) + weight < dist.get(v)) {
                    dist.put(v, dist.get(u) + weight);
                    prev.put(v, u);
                    pq.offer(new int[]{v, dist.get(v)});
                }
            }
        }

        List<Integer> path = new LinkedList<>();
        if (dist.get(target) == Integer.MAX_VALUE) return path; // unreachable
        Integer step = target;
        while (step != null) {
            path.add(0, step);
            step = prev.get(step);
        }
        return path;
    }

    /** BFS traversal -- demonstrates network connectivity/reachability. O(V + E). */
    public List<Integer> bfsTraversal(int start) {
        List<Integer> order = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();
        queue.offer(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            int curr = queue.poll();
            order.add(curr);
            for (int[] edge : adjacency.get(curr)) {
                int neighbor = edge[0];
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.offer(neighbor);
                }
            }
        }
        return order;
    }

    /** Simple console-based ASCII visualization of the graph's adjacency structure. */
    public void renderAdjacency() {
        System.out.println("\n--- Logistics Network (Adjacency View) ---");
        for (int id : nodeNames.keySet()) {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("[%s]", nameOf(id)));
            for (int[] edge : adjacency.get(id)) {
                sb.append(String.format("  --(%d)--> %s", edge[1], nameOf(edge[0])));
            }
            System.out.println(sb);
        }
    }

    /** Renders a shortest path as an arrow-chain, e.g. Warehouse -> CityA -> CityB. */
    public String renderPath(List<Integer> path) {
        if (path.isEmpty()) return "UNREACHABLE";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < path.size(); i++) {
            sb.append(nameOf(path.get(i)));
            if (i != path.size() - 1) sb.append("  ==>  ");
        }
        return sb.toString();
    }
}
