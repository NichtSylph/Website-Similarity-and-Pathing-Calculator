package websimilaritiespj3;

import java.util.ArrayList;

/**
 * Holds the graph data structure for the website comparison application.
 * This includes a list of site records and a list of edges between these sites.
 * Provides methods to manipulate and access the graph data.
 * 
 * @author Joel Santos
 * @version 3.0
 * @since 11-10-2023
 */
public class GraphData {
    private ArrayList<SiteRecord> sites; // List of all sites
    private ArrayList<SiteEdge> edges; // List of all edges

    /**
     * Constructs a new, empty GraphData object.
     */
    public GraphData() {
        sites = new ArrayList<>();
        edges = new ArrayList<>();
    }

    /**
     * Adds a site record to the graph.
     * 
     * @param site The SiteRecord object to add.
     */
    public void addSite(SiteRecord site) {
        sites.add(site);
    }

    /**
     * Adds an edge to the graph.
     * 
     * @param edge The SiteEdge object to add.
     */
    public void addEdge(SiteEdge edge) {
        edges.add(edge);
    }

    /**
     * Retrieves the list of site records in the graph.
     * 
     * @return An ArrayList of SiteRecord objects.
     */
    public ArrayList<SiteRecord> getSites() {
        return sites;
    }

    /**
     * Retrieves the list of edges in the graph.
     * 
     * @return An ArrayList of SiteEdge objects.
     */
    public ArrayList<SiteEdge> getEdges() {
        return edges;
    }
}
