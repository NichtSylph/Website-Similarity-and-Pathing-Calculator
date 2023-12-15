package websimilaritiespj3;

import java.util.PriorityQueue;
import java.util.Set;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * Manages the graph structure for the website similarity application.
 * This class handles operations such as building the graph, finding the
 * shortest path, and managing disjoint sets.
 * 
 * @author Joel Santos
 * @version 3.0
 * @since 11-10-2023
 */
public class GraphManager {
    private GraphData graphData; // Graph data containing sites and edges
    private HT<String, SiteRecord> siteMap; // Maps site URLs to SiteRecord objects
    private HT<String, SerializableList<SiteEdge>> adjacencyList;// Adjacency list representation of the graph
    private UnionFind unionFind; // UnionFind for disjoint set operations

    /**
     * Constructs a GraphManager with the provided graph data.
     * 
     * @param graphData The GraphData object to manage.
     */
    public GraphManager(GraphData graphData) {
        this.graphData = graphData;
        this.siteMap = new HT<>();
        this.adjacencyList = new HT<>();
        buildSiteMap();
        buildAdjacencyList();
        initializeUnionFind();
    }

    /**
     * Builds a map from site URLs to SiteRecord objects.
     */
    private void buildSiteMap() {
        for (SiteRecord site : graphData.getSites()) {
            siteMap.put(site.getUrl(), site);
        }
    }

    /**
     * Builds the adjacency list for the graph.
     */
    private void buildAdjacencyList() {
        for (SiteEdge edge : graphData.getEdges()) {
            String site1 = edge.getSite1();
            String site2 = edge.getSite2();

            adjacencyList.putIfAbsent(site1, new SerializableList<>());
            adjacencyList.putIfAbsent(site2, new SerializableList<>());

            adjacencyList.get(site1).add(edge);
            adjacencyList.get(site2).add(edge);
        }
    }

    /**
     * Initializes the UnionFind data structure with the graph's vertices.
     */
    private void initializeUnionFind() {
        Set<String> vertices = new HashSet<>();
        for (String url : siteMap.keySet()) {
            vertices.add(url);
        }
        unionFind = new UnionFind(vertices);
    }

    /**
     * Updates the graph structure after modifications.
     */
    public void updateGraphStructure() {
        buildSiteMap();
        buildAdjacencyList();
        initializeUnionFind();

        for (SiteEdge edge : graphData.getEdges()) {
            unionFind.union(edge.getSite1(), edge.getSite2());
        }
    }

    /**
     * Finds the shortest path between two sites using Dijkstra's algorithm.
     * 
     * @param fromSite The starting site URL.
     * @param toSite   The destination site URL.
     * @return A list of SiteEdge objects representing the shortest path.
     */
    public List<SiteEdge> findShortestPath(String fromSite, String toSite) {
        if (!siteMap.contains(fromSite) || !siteMap.contains(toSite)) {
            return new SerializableList<>(); // Return an empty list if either site is not in the graph
        }

        HT<String, Double> distances = new HT<>();
        HT<String, SiteEdge> edgeTo = new HT<>();
        PriorityQueue<SiteRecord> pq = new PriorityQueue<>(
                (a, b) -> Double.compare(distances.getOrDefault(a.getUrl(), Double.MAX_VALUE),
                        distances.getOrDefault(b.getUrl(), Double.MAX_VALUE)));

        for (String url : siteMap.keySet()) {
            distances.put(url, Double.MAX_VALUE);
        }
        distances.put(fromSite, 0.0);

        pq.add(siteMap.get(fromSite));

        while (!pq.isEmpty()) {
            SiteRecord current = pq.poll();
            SerializableList<SiteEdge> edges = adjacencyList.getOrDefault(current.getUrl(), new SerializableList<>());

            for (SiteEdge edge : edges) {
                String neighborUrl = edge.getOtherSite(current.getUrl());
                SiteRecord neighbor = siteMap.get(neighborUrl);
                double newDist = distances.get(current.getUrl()) + edge.getSimilarityScore();

                if (newDist < distances.getOrDefault(neighborUrl, Double.MAX_VALUE)) {
                    distances.put(neighborUrl, newDist);
                    edgeTo.put(neighborUrl, edge);
                    pq.add(neighbor);
                }
            }
        }

        SerializableList<SiteEdge> path = new SerializableList<>();
        for (String at = toSite; edgeTo.contains(at); at = edgeTo.get(at).getOtherSite(at)) {
            path.add(edgeTo.get(at));
        }
        Collections.reverse(path);
        return path;
    }

    /**
     * Returns the number of disjoint sets in the graph.
     * 
     * @return The number of disjoint sets.
     */
    public int getNumberOfDisjointSets() {
        return unionFind.countDisjointSets();
    }
}
