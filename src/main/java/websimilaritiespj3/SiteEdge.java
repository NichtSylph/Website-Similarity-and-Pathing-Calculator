package websimilaritiespj3;

import java.io.Serializable;

/**
 * Represents an edge in a graph, connecting two websites (sites) and holding a
 * similarity score between them.
 * This class is part of the website comparison application.
 * 
 * @author Joel Santos
 * @version 3.0
 * @since 11-10-2023
 */
public class SiteEdge implements Serializable {
    private String site1;
    private String site2;
    private double similarityScore;

    /**
     * Constructs a SiteEdge object with specified sites and similarity score.
     * 
     * @param site1           The URL of the first site.
     * @param site2           The URL of the second site.
     * @param similarityScore The similarity score between the two sites.
     */
    public SiteEdge(String site1, String site2, double similarityScore) {
        this.site1 = site1;
        this.site2 = site2;
        this.similarityScore = similarityScore;
    }

    /**
     * Gets the URL of the first site.
     * 
     * @return The URL of the first site.
     */
    public String getSite1() {
        return site1;
    }

    /**
     * Gets the URL of the second site.
     * 
     * @return The URL of the second site.
     */
    public String getSite2() {
        return site2;
    }

    /**
     * Gets the similarity score of this edge.
     * 
     * @return The similarity score.
     */
    public double getSimilarityScore() {
        return similarityScore;
    }

    /**
     * Gets the URL of the site opposite to the given site on this edge.
     * 
     * @param siteUrl The URL of one of the sites on the edge.
     * @return The URL of the other site on the edge. Returns null if the provided
     *         URL is not part of the edge.
     */
    public String getOtherSite(String siteUrl) {
        if (siteUrl.equals(site1)) {
            return site2;
        } else if (siteUrl.equals(site2)) {
            return site1;
        }
        return null; // or throw an exception if the siteUrl is not part of the edge
    }
}
