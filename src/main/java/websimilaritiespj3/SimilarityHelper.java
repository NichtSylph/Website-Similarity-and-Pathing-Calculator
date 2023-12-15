package websimilaritiespj3;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Map.Entry;

/**
 * Helper class for managing clusters and calculating similarities between URLs.
 * 
 * @author Joel Santos
 * @version 3.0
 * @since 11-10-2023
 */
public class SimilarityHelper {

    // Hash tables for storing clusters, frequency tables, and cluster keys
    private static HT<String, ArrayList<String>> clusters = new HT<>();
    private static HT<String, FrequencyTable> urlToFrequencyTableMap = new HT<>();
    private static HT<String, String> urlToClusterKeyMap = new HT<>();

    /**
     * Initializes clusters with the given frequency tables.
     * 
     * @param frequencyTables The frequency tables to be used for initializing
     *                        clusters.
     */
    public static void initializeClusters(HT<String, FrequencyTable> frequencyTables) {
        urlToFrequencyTableMap = frequencyTables;
    }

    /**
     * Finds the most similar URLs to the given URL within its cluster.
     * 
     * @param url  The URL to find similar URLs to.
     * @param topN The number of top similar URLs to return.
     * @return A list of the most similar URLs.
     */
    public static List<String> findMostSimilarUrls(String url, int topN) {
        // Get the cluster key for the given URL
        String clusterKey = getClusterKeyForUrl(url);
        if (clusterKey == null) {
            return Collections.emptyList();
        }

        // Retrieve cluster members
        List<String> clusterMembers = clusters.get(clusterKey);
        if (clusterMembers == null) {
            return Collections.emptyList();
        }

        // Get the frequency table for the given URL
        FrequencyTable urlFrequencyTable = urlToFrequencyTableMap.get(url);
        if (urlFrequencyTable == null) {
            return Collections.emptyList();
        }

        // Calculate similarities and store them in a priority queue
        PriorityQueue<Entry<String, Double>> similarityQueue = new PriorityQueue<>(
                (entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        for (String memberUrl : clusterMembers) {
            if (!memberUrl.equals(url)) {
                FrequencyTable memberFrequencyTable = urlToFrequencyTableMap.get(memberUrl);
                if (memberFrequencyTable == null) {
                    continue;
                }
                double similarity = SimilarityMetricCalculator.calculateCosineSimilarity(urlFrequencyTable,
                        memberFrequencyTable);
                similarityQueue.add(new AbstractMap.SimpleEntry<>(memberUrl, similarity));
            }
        }

        // Retrieve the top N similar URLs
        List<String> mostSimilarUrls = new ArrayList<>();
        for (int i = 0; i < topN && !similarityQueue.isEmpty(); i++) {
            mostSimilarUrls.add(similarityQueue.poll().getKey());
        }

        return mostSimilarUrls;
    }

    /**
     * Retrieves the cluster key associated with a given URL.
     * 
     * @param url The URL for which the cluster key is to be retrieved.
     * @return The cluster key or null if not found.
     */
    private static String getClusterKeyForUrl(String url) {
        return urlToClusterKeyMap.getOrDefault(url, null);
    }

    /**
     * Finds the shortest path between two sites using Dijkstra's algorithm.
     * 
     * @param graph The graph data containing the sites and edges.
     * @param site1 The starting site.
     * @param site2 The destination site.
     * @return A list of SiteEdges representing the shortest path.
     */
    public static List<SiteEdge> findShortestPath(GraphData graph, String site1, String site2) {
        HT<String, SiteRecord> siteMap = new HT<>();
        for (SiteRecord site : graph.getSites()) {
            siteMap.put(site.getUrl(), site);
        }

        HT<String, Double> dist = new HT<>();
        HT<String, SiteEdge> prev = new HT<>();
        PriorityQueue<SiteRecord> pq = new PriorityQueue<>(
                (a, b) -> Double.compare(dist.getOrDefault(a.getUrl(), Double.MAX_VALUE),
                        dist.getOrDefault(b.getUrl(), Double.MAX_VALUE)));

        dist.put(site1, 0.0);
        pq.add(siteMap.get(site1));

        while (!pq.isEmpty()) {
            SiteRecord current = pq.poll();
            if (current.getUrl().equals(site2)) {
                break;
            }

            for (SiteEdge edge : current.getAdjacentEdges()) {
                String neighborUrl = edge.getOtherSite(current.getUrl());
                if (neighborUrl != null) {
                    double newDist = dist.getOrDefault(current.getUrl(), Double.MAX_VALUE) + edge.getSimilarityScore();
                    if (newDist < dist.getOrDefault(neighborUrl, Double.MAX_VALUE)) {
                        dist.put(neighborUrl, newDist);
                        prev.put(neighborUrl, edge);
                        pq.add(siteMap.get(neighborUrl));
                    }
                }
            }
        }

        return reconstructPath(site1, site2, prev);
    }

    /**
     * Reconstructs the shortest path between two sites.
     * 
     * @param start The starting site.
     * @param end   The destination site.
     * @param prev  The hashtable containing the previous edges.
     * @return A list of SiteEdges representing the shortest path.
     */
    private static List<SiteEdge> reconstructPath(String start, String end, HT<String, SiteEdge> prev) {
        List<SiteEdge> path = new ArrayList<>();
        for (String at = end; prev.contains(at); at = prev.get(at).getOtherSite(at)) {
            path.add(prev.get(at));
        }
        Collections.reverse(path);
        return path;
    }
}
