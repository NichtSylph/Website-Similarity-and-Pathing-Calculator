package websimilaritiespj3;

import java.util.Set;

/**
 * Implements a Union-Find data structure with path compression.
 * Used for managing disjoint sets, particularly useful in graph algorithms.
 *
 * @author Joel Santos
 * @version 3.0
 * @since 11-10-2023
 */
public class UnionFind {
    private HT<String, String> parent;

    /**
     * Constructs a UnionFind instance with each vertex as its own parent initially.
     *
     * @param vertices A set of vertices to initialize the UnionFind structure.
     */
    public UnionFind(Set<String> vertices) {
        parent = new HT<>();
        for (String vertex : vertices) {
            parent.put(vertex, vertex); // Initially, each vertex is its own parent
        }
    }

    /**
     * Finds the representative (root) of the set that contains the given element.
     * Applies path compression to optimize future searches.
     *
     * @param element The element whose set representative is to be found.
     * @return The root of the set containing the element.
     */
    public String find(String element) {
        if (!parent.contains(element)) {
            return null;
        }
        String root = element;
        while (!root.equals(parent.get(root))) {
            root = parent.get(root);
        }
        // Path compression for efficiency
        while (!element.equals(root)) {
            String next = parent.get(element);
            parent.put(element, root);
            element = next;
        }
        return root;
    }

    /**
     * Merges the sets containing the two elements into a single set.
     *
     * @param a The first element to be merged.
     * @param b The second element to be merged.
     */
    public void union(String a, String b) {
        String rootA = find(a);
        String rootB = find(b);
        if (rootA != null && rootB != null && !rootA.equals(rootB)) {
            parent.put(rootA, rootB);
        }
    }

    /**
     * Counts the number of disjoint sets present in the UnionFind structure.
     *
     * @return The number of disjoint sets.
     */
    public int countDisjointSets() {
        HT<String, Boolean> roots = new HT<>();
        for (String element : parent.keySet()) {
            String root = find(element);
            roots.putIfAbsent(root, true);
        }
        // Manually count the number of unique roots
        int count = 0;
        for (Boolean exists : roots.values()) {
            if (exists) {
                count++;
            }
        }
        return count;
    }
}
