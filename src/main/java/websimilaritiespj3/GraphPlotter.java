package websimilaritiespj3;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.Color;
import java.util.List;
import java.util.Set;
import javax.swing.JPanel;

/**
 * Responsible for plotting graphs based on the similarity metrics.
 * This class uses JFreeChart to create scatter plots representing the
 * similarities between websites.
 * 
 * @author Joel Santos
 * @version 3.0
 * @since 11-10-2023
 */
public class GraphPlotter {
    private HT<String, Integer> urlToIndexMap; // Maps URLs to their respective indices for plotting
    private int currentIndex = 0; // Current index for assigning to new URLs
    private List<String> mostSimilarUrls; // List of URLs considered most similar to the user's input

    /**
     * Constructs a GraphPlotter with an empty URL to index map.
     */
    public GraphPlotter() {
        this.urlToIndexMap = new HT<>();
    }

    /**
     * Retrieves or assigns an index for the given URL.
     * 
     * @param url The URL for which to get the index.
     * @return The index associated with the URL.
     */
    private int getIndexForURL(String url) {
        Integer index = urlToIndexMap.get(url);
        if (index == null) {
            urlToIndexMap.put(url, currentIndex++);
        }
        return urlToIndexMap.get(url);
    }

    /**
     * Sets the list of most similar URLs.
     * 
     * @param mostSimilarUrls The list of most similar URLs.
     */
    public void setMostSimilarUrls(List<String> mostSimilarUrls) {
        this.mostSimilarUrls = mostSimilarUrls;
    }

    /**
     * Generates scatter plot datasets from the given frequency tables.
     * 
     * @param frequencyTables The frequency tables to use for generating the
     *                        datasets.
     * @param userInput       The user input URL for comparison.
     * @return A hashtable of scatter plot datasets.
     */
    public HT<String, XYSeriesCollection> generateScatterPlotDatasets(
            HT<String, FrequencyTable> frequencyTables, String userInput) {
        HT<String, XYSeriesCollection> datasets = new HT<>();

        if (frequencyTables == null || frequencyTables.isEmpty()) {
            return datasets;
        }

        XYSeriesCollection cosineDataset = new XYSeriesCollection();
        Set<HT.Node<String, FrequencyTable>> entries = frequencyTables.entrySet();

        XYSeries userSeries = new XYSeries("User Input");
        XYSeries similarSeries = new XYSeries("Most Similar URLs");
        XYSeries otherSeries = new XYSeries("Other URLs");

        // Obtain the frequency table for the user input URL
        FrequencyTable userInputTable = frequencyTables.get(userInput);

        for (HT.Node<String, FrequencyTable> entry : entries) {
            int index = getIndexForURL(entry.key);
            double similarity;
            if (userInput != null && entry.key.equals(userInput)) {
                similarity = 1.0; // The similarity of the URL with itself is 1
            } else if (userInputTable != null) {
                similarity = SimilarityMetricCalculator.calculateCosineSimilarity(entry.value, userInputTable);
            } else {
                similarity = 0; // If there's no user input, default to 0
            }

            if (entry.key.equals(userInput)) {
                userSeries.add(index, similarity);
            } else if (mostSimilarUrls != null && mostSimilarUrls.contains(entry.key)) {
                similarSeries.add(index, similarity);
            } else {
                otherSeries.add(index, similarity);
            }

            System.out.println("URL Index: " + index + ", URL: " + entry.key + ", Similarity: " + similarity);
        }

        cosineDataset.addSeries(userSeries);
        cosineDataset.addSeries(similarSeries);
        cosineDataset.addSeries(otherSeries);

        datasets.put("Cosine Similarity", cosineDataset);

        return datasets;
    }

    /**
     * Creates a scatter plot panel with the given dataset and title.
     * 
     * @param dataset The dataset for the scatter plot.
     * @param title   The title of the scatter plot.
     * @return A ChartPanel containing the scatter plot.
     */
    public ChartPanel createScatterPlotPanel(XYSeriesCollection dataset, String title) {
        JFreeChart scatterPlot = ChartFactory.createScatterPlot(
                title, "URL Index", "Similarity Score", dataset, PlotOrientation.VERTICAL, true, true, false);

        XYPlot plot = scatterPlot.getXYPlot();
        XYItemRenderer renderer = plot.getRenderer();
        renderer.setSeriesPaint(dataset.indexOf("User Input"), Color.RED);
        renderer.setSeriesPaint(dataset.indexOf("Most Similar URLs"), Color.BLUE);
        renderer.setSeriesPaint(dataset.indexOf("Other URLs"), Color.GREEN);

        return new ChartPanel(scatterPlot);
    }

    /**
     * Updates the graph in the provided panel based on the given URLs and frequency
     * tables.
     * 
     * @param urls            The list of URLs to include in the graph.
     * @param frequencyTables The frequency tables of the URLs.
     * @param graphPanel      The JPanel to update with the new graph.
     */
    public void updateGraph(List<String> urls, HT<String, FrequencyTable> frequencyTables, JPanel graphPanel) {
        HT<String, XYSeriesCollection> datasets = new HT<>();
        for (String url : urls) {
            datasets.putAll(generateScatterPlotDatasets(frequencyTables, url));
        }

        graphPanel.removeAll();
        for (HT.Node<String, XYSeriesCollection> entry : datasets.entrySet()) {
            ChartPanel panel = createScatterPlotPanel(entry.value, entry.key);
            graphPanel.add(panel);
        }

        graphPanel.revalidate();
        graphPanel.repaint();
    }
}
