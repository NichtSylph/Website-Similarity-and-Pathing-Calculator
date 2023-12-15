package websimilaritiespj3;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a record of a website, including its URL, frequency table,
 * similarity score, and adjacent edges.
 * This class is part of the website comparison application.
 * 
 * @author Joel Santos
 * @version 3.0
 * @since 11-10-2023
 */
public class SiteRecord implements Serializable {
    private FrequencyTable frequencyTable;
    private double similarityScore;
    private String url;
    private List<SiteEdge> adjacentEdges;

    /**
     * Constructs a SiteRecord object with specified URL, frequency table, and
     * similarity score.
     * 
     * @param url             The URL of the site.
     * @param frequencyTable  The frequency table associated with the site.
     * @param similarityScore The similarity score of the site.
     */
    public SiteRecord(String url, FrequencyTable frequencyTable, double similarityScore) {
        this.url = url;
        this.frequencyTable = frequencyTable;
        this.similarityScore = similarityScore;
        this.adjacentEdges = new ArrayList<>();
    }

    /**
     * Gets the URL of the site.
     * 
     * @return The URL of the site.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Gets the frequency table of the site.
     * 
     * @return The frequency table of the site.
     */
    public FrequencyTable getFrequencyTable() {
        return frequencyTable;
    }

    /**
     * Gets the similarity score of the site.
     * 
     * @return The similarity score of the site.
     */
    public double getSimilarityScore() {
        return similarityScore;
    }

    /**
     * Sets the frequency table of the site.
     * 
     * @param frequencyTable The new frequency table to set.
     */
    public void setFrequencyTable(FrequencyTable frequencyTable) {
        this.frequencyTable = frequencyTable;
    }

    /**
     * Sets the similarity score of the site.
     * 
     * @param similarityScore The new similarity score to set.
     */
    public void setSimilarityScore(double similarityScore) {
        this.similarityScore = similarityScore;
    }

    /**
     * Adds an adjacent edge to the site record.
     * 
     * @param edge The SiteEdge to be added.
     */
    public void addAdjacentEdge(SiteEdge edge) {
        adjacentEdges.add(edge);
    }

    /**
     * Gets a list of adjacent edges of the site.
     * 
     * @return A list of adjacent SiteEdges.
     */
    public List<SiteEdge> getAdjacentEdges() {
        return adjacentEdges;
    }
}
