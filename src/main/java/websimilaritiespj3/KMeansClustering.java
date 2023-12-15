package websimilaritiespj3;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class implements the K-Means clustering algorithm for clustering
 * frequency tables.
 * It groups frequency tables into a specified number of clusters based on their
 * similarity.
 * 
 * @author Joel Santos
 * @version 3.0
 * @since 11-10-2023
 */
public class KMeansClustering {
    private int k; // Number of clusters
    private List<FrequencyTable> data; // The data to be clustered
    private List<FrequencyTable> centroids; // The centroids of the clusters

    /**
     * Constructor for KMeansClustering.
     *
     * @param data The data (list of frequency tables) to be clustered.
     */
    public KMeansClustering(List<FrequencyTable> data) {
        this.k = 10; // Default number of clusters
        this.data = data;
        this.centroids = initializeCentroids();
    }

    /**
     * Initializes centroids randomly from the data points.
     * 
     * @return A list of initial centroids.
     */
    private List<FrequencyTable> initializeCentroids() {
        List<FrequencyTable> initialCentroids = new ArrayList<>();
        Random rand = new Random();
        for (int i = 0; i < k; i++) {
            initialCentroids.add(data.get(rand.nextInt(data.size())));
        }
        return initialCentroids;
    }

    /**
     * Executes the clustering process.
     * 
     * @return A mapping of centroids to their corresponding clusters (group of
     *         frequency tables).
     */
    public HT<FrequencyTable, ArrayList<FrequencyTable>> cluster() {
        HT<FrequencyTable, ArrayList<FrequencyTable>> clusters = new HT<>();

        boolean centroidsChanged;
        do {
            // Clear existing clusters and reassign data points to nearest centroids
            clusters.clear();
            for (FrequencyTable centroid : centroids) {
                clusters.put(centroid, new ArrayList<>());
            }

            for (FrequencyTable dataPoint : data) {
                FrequencyTable nearestCentroid = findNearestCentroid(dataPoint);
                clusters.get(nearestCentroid).add(dataPoint);
            }

            // Recalculate centroids and check for changes
            centroidsChanged = false;
            List<FrequencyTable> newCentroids = recalculateCentroids(clusters);
            for (int i = 0; i < centroids.size(); i++) {
                if (!newCentroids.get(i).equals(centroids.get(i))) {
                    centroidsChanged = true;
                    break;
                }
            }
            centroids = newCentroids;
        } while (centroidsChanged);

        return clusters;
    }

    /**
     * Finds the nearest centroid for a given data point.
     * 
     * @param dataPoint The data point for which to find the nearest centroid.
     * @return The nearest centroid.
     */
    private FrequencyTable findNearestCentroid(FrequencyTable dataPoint) {
        FrequencyTable nearestCentroid = null;
        double maxSimilarity = Double.NEGATIVE_INFINITY;
        for (FrequencyTable centroid : centroids) {
            double similarity = SimilarityMetricCalculator.calculateCosineSimilarity(dataPoint, centroid);
            if (similarity > maxSimilarity) {
                maxSimilarity = similarity;
                nearestCentroid = centroid;
            }
        }
        return nearestCentroid;
    }

    /**
     * Recalculates the centroids based on the current clusters.
     * 
     * @param clusters The current clusters.
     * @return A list of new centroids.
     */
    private List<FrequencyTable> recalculateCentroids(HT<FrequencyTable, ArrayList<FrequencyTable>> clusters) {
        List<FrequencyTable> newCentroids = new ArrayList<>();
        for (HT.Node<FrequencyTable, ArrayList<FrequencyTable>> entry : clusters.entrySet()) {
            FrequencyTable newCentroid = calculateAverageCentroid(entry.value);
            newCentroids.add(newCentroid);
        }
        return newCentroids;
    }

    /**
     * Calculates the average centroid of a given cluster.
     * 
     * @param cluster The cluster for which to calculate the average centroid.
     * @return The average centroid.
     */
    private FrequencyTable calculateAverageCentroid(ArrayList<FrequencyTable> cluster) {
        FrequencyTable averageCentroid = new FrequencyTable();
        for (FrequencyTable dataPoint : cluster) {
            averageCentroid.merge(dataPoint);
        }
        averageCentroid.divide(cluster.size());
        return averageCentroid;
    }
}
