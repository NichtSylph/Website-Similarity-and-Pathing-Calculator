package websimilaritiespj3;

import org.jfree.chart.ChartPanel;
import org.jfree.data.xy.XYSeriesCollection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * The GUI for the Website Comparison Application.
 * This class manages the graphical user interface, including URL addition,
 * graph visualization, and shortest path computation between websites.
 *
 * @author Joel Santos
 * @version 3.0
 * @since 11-10-2023
 */
public class SimilarityGUI extends JFrame {
    private JTextField addURLField;
    private JButton addURLButton;
    private JButton findPathButton;
    private JComboBox<String> fromURLDropdown;
    private JComboBox<String> toURLDropdown;
    private String urlsFilePath = "C:\\Users\\joels\\OneDrive\\Oswego\\Fall 2023\\CSC365\\PJ3-JS\\websimilaritiespj3\\data\\Urls.txt";
    private String frequencyTableMapFilePath = "C:\\Users\\joels\\OneDrive\\Oswego\\Fall 2023\\CSC365\\PJ3-JS\\websimilaritiespj3\\data\\frequencyTableMap.ser";
    private ArrayList<String> fileURLs;
    private HT<String, FrequencyTable> urlToFrequencyTableMap;
    private JPanel graphPanel;
    private GraphPlotter graphPlotter;
    private GraphData graphData;
    private GraphManager graphManager;
    private DataPersistenceManager dataPersistenceManager;
    private ExecutorService executorService = Executors.newCachedThreadPool();

    /**
     * Constructor for SimilarityGUI. Initializes the GUI components and loads data.
     */
    public SimilarityGUI() {
        super("Website Comparison Application by Joel S.");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1600, 900);
        setLocationRelativeTo(null);

        this.graphData = new GraphData();
        this.graphManager = new GraphManager(graphData);
        this.dataPersistenceManager = new DataPersistenceManager();
        this.graphPlotter = new GraphPlotter();
        this.fileURLs = new ArrayList<>();

        loadFrequencyTablesAndURLs();
        initComponents();
        displayInitialGraph();

        setVisible(true);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                serializeHashTable(urlToFrequencyTableMap);
                executorService.shutdown();
            }
        });
    }

    /**
     * Loads frequency tables and URLs from files.
     */
    private void loadFrequencyTablesAndURLs() {
        boolean rebuilt = checkAndRebuildSerializedFile(frequencyTableMapFilePath, fileURLs);
        if (rebuilt) {
            System.out.println("Serialized file was rebuilt.");
        }

        urlToFrequencyTableMap = deserializeOrReserializeHashTable();
        if (urlToFrequencyTableMap == null) {
            urlToFrequencyTableMap = new HT<>();
        }

        fileURLs = loadURLs();
        if (urlToFrequencyTableMap.isEmpty()) {
            urlToFrequencyTableMap = createFrequencyTables(fileURLs);
        }
    }

    /**
     * Loads URLs from a specified file.
     *
     * @return A list of URLs.
     */
    private ArrayList<String> loadURLs() {
        try {
            return new ArrayList<>(dataPersistenceManager.loadURLsFromFile(urlsFilePath));
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Initializes the components of the GUI.
     */
    private void initComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        addURLField = new JTextField(30);
        addURLButton = new JButton("Add");
        addURLButton.setPreferredSize(new Dimension(100, addURLButton.getPreferredSize().height));
        addURLButton.addActionListener(e -> addWebsiteURL());

        findPathButton = new JButton("Find Path");

        // Initializing the dropdowns
        fromURLDropdown = new JComboBox<>();
        toURLDropdown = new JComboBox<>();
        for (String url : fileURLs) {
            fromURLDropdown.addItem(url);
            toURLDropdown.addItem(url);
        }

        findPathButton.addActionListener(e -> displayShortestPath(
                (String) fromURLDropdown.getSelectedItem(),
                (String) toURLDropdown.getSelectedItem()));

        gbc.insets = new Insets(5, 5, 5, 5); // Added padding
        gbc.anchor = GridBagConstraints.LINE_START; // Align to the center

        // Adding components to the panel
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Span two columns for the label
        panel.add(new JLabel("Add URL:"), gbc);

        gbc.gridx = 2;
        gbc.gridwidth = 3; // Span three columns for the text field
        panel.add(addURLField, gbc);

        gbc.gridx = 5;
        gbc.gridwidth = 1; // Span one column for the button
        panel.add(addURLButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2; // Span two columns for the label
        panel.add(new JLabel("From:"), gbc);

        gbc.gridx = 2;
        gbc.gridwidth = 1; // Span one column for the dropdown
        panel.add(fromURLDropdown, gbc); // Added dropdown for "From" URL

        gbc.gridx = 3;
        gbc.gridwidth = 2; // Span two columns for the label
        panel.add(new JLabel("To:"), gbc);

        gbc.gridx = 5;
        gbc.gridwidth = 1; // Span one column for the dropdown
        panel.add(toURLDropdown, gbc); // Added dropdown for "To" URL

        gbc.gridx = 6;
        gbc.gridwidth = 1; // Span one column for the button
        panel.add(findPathButton, gbc);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(panel, BorderLayout.NORTH);

        graphPanel = new JPanel(new BorderLayout());
        getContentPane().add(graphPanel, BorderLayout.CENTER);
    }

    /**
     * Checks and rebuilds the serialized file if necessary.
     *
     * @param filePath The path to the serialized file.
     * @param urls     The list of URLs to process.
     * @return True if the file was rebuilt, false otherwise.
     */
    private boolean checkAndRebuildSerializedFile(String filePath, List<String> urls) {
        File file = new File(filePath);
        if (!file.exists() || file.length() == 0) {
            rebuildSerializedFile(urls, filePath);
            return true;
        }
        return false;
    }

    /**
     * Rebuilds the serialized file from a list of URLs.
     *
     * @param urls     The list of URLs.
     * @param filePath The path to the serialized file.
     */
    private void rebuildSerializedFile(List<String> urls, String filePath) {
        if (urls == null) {
            System.err.println("URL list is null in rebuildSerializedFile.");
            return;
        }
        HT<String, FrequencyTable> frequencyTableMap = new HT<>();
        for (String url : urls) {
            FrequencyTable table = new FrequencyTable();
            table.addWordsFromURL(url);
            frequencyTableMap.put(url, table);
        }
        serializeHashTable(frequencyTableMap);
    }

    /**
     * Adds a new website URL and updates the graph.
     */
    private void addWebsiteURL() {
        String newURL = addURLField.getText().trim();
        if (!newURL.isEmpty() && !urlToFrequencyTableMap.contains(newURL)) {
            FrequencyTable newTable = new FrequencyTable();
            newTable.addWordsFromURL(newURL);
            urlToFrequencyTableMap.put(newURL, newTable);
            updateGraphData(newURL, newTable);
            fileURLs.add(newURL);

            // Update dropdowns with the new URL
            fromURLDropdown.addItem(newURL);
            toURLDropdown.addItem(newURL);

            appendURLToFile(newURL);
            displayGraph(newURL); // Update the graph with the new URL
            addURLField.setText(""); // Clear the input field
        }
    }

    /**
     * Updates the graph data with a new URL and frequency table.
     *
     * @param newURL   The new URL to add.
     * @param newTable The frequency table for the new URL.
     */
    private void updateGraphData(String newURL, FrequencyTable newTable) {
        SiteRecord newSite = new SiteRecord(newURL, newTable, 0);
        graphData.addSite(newSite);

        for (String existingURL : fileURLs) {
            FrequencyTable existingTable = urlToFrequencyTableMap.get(existingURL);

            // Debug print to check table contents
            System.out.println("Comparing: " + newURL + " with " + existingURL);
            System.out.println("New Table (" + newURL + "): " + newTable);
            System.out.println("Existing Table (" + existingURL + "): " + existingTable);

            double similarityScore = SimilarityMetricCalculator.calculateCosineSimilarity(newTable, existingTable);
            SiteEdge edge = new SiteEdge(newURL, existingURL, similarityScore);
            graphData.addEdge(edge);
        }

        graphManager.updateGraphStructure();
    }

    /**
     * Appends a new URL to the file.
     *
     * @param url The URL to append.
     */
    private void appendURLToFile(String url) {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(urlsFilePath, true)))) {
            out.println(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Displays the initial graph based on loaded data.
     */
    private void displayInitialGraph() {
        // This method initializes the graph based on the loaded URLs
        if (!fileURLs.isEmpty()) {
            for (String url : fileURLs) {
                updateGraphData(url, urlToFrequencyTableMap.get(url));
            }
            displayGraph(null); // Display the graph with the initial data
        } else {
            System.out.println("No URLs loaded from file.");
        }
    }

    /**
     * Displays the graph based on the user input.
     *
     * @param userInput The user input URL for graph display.
     */
    private void displayGraph(String userInput) {
        HT<String, FrequencyTable> frequencyTables = new HT<>();
        for (SiteRecord site : graphData.getSites()) {
            frequencyTables.put(site.getUrl(), site.getFrequencyTable());
        }

        if (frequencyTables.isEmpty()) {
            System.out.println("Frequency tables are empty.");
            return;
        }

        HT<String, XYSeriesCollection> datasets = graphPlotter.generateScatterPlotDatasets(frequencyTables, userInput);

        if (datasets == null || datasets.isEmpty()) {
            System.out.println("Datasets for the graph are empty.");
            return;
        }

        XYSeriesCollection dataset;
        if (userInput == null || userInput.trim().isEmpty()) {
            System.out.println("User input is null or empty, using default dataset.");
            dataset = getDefaultDataset(datasets);
        } else {
            dataset = datasets.get(userInput);
            if (dataset == null) {
                System.out.println("No dataset found for the user input: " + userInput);
                dataset = getDefaultDataset(datasets); // Fallback to default if needed
            }
        }

        graphPanel.removeAll();
        ChartPanel chartPanel = graphPlotter.createScatterPlotPanel(dataset, "URL Cosine Similarity Scatter Plot");
        graphPanel.add(chartPanel, BorderLayout.CENTER);
        graphPanel.revalidate();
        graphPanel.repaint();

        System.out.println("User input for graph display: " + userInput);
        System.out.println("Available datasets keys: " + datasets.keySet());
    }

    /**
     * Displays the shortest path between two URLs.
     *
     * @param fromUrl The starting URL.
     * @param toUrl   The destination URL.
     */
    private void displayShortestPath(String fromUrl, String toUrl) {
        List<SiteEdge> shortestPath = graphManager.findShortestPath(fromUrl, toUrl);

        if (shortestPath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No path found between " + fromUrl + " and " + toUrl);
        } else {
            StringBuilder pathDisplay = new StringBuilder("Shortest path:\n");
            for (SiteEdge edge : shortestPath) {
                pathDisplay.append(edge.getSite1()).append(" -> ").append(edge.getSite2())
                        .append(" (Score: ").append(edge.getSimilarityScore()).append(")\n");
            }
            JOptionPane.showMessageDialog(this, pathDisplay.toString());
        }
    }

    /**
     * Gets the default dataset for the graph.
     *
     * @param datasets The datasets to choose from.
     * @return The default dataset.
     */
    private XYSeriesCollection getDefaultDataset(HT<String, XYSeriesCollection> datasets) {
        return datasets.values().iterator().next();
    }

    private void serializeHashTable(HT<String, FrequencyTable> table) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(frequencyTableMapFilePath))) {
            out.writeObject(table);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Serializes the hash table to a file.
     *
     * @param table The hash table to serialize.
     */
    @SuppressWarnings("unchecked")
    private HT<String, FrequencyTable> deserializeOrReserializeHashTable() {
        File serializationFile = new File(frequencyTableMapFilePath);

        if (!serializationFile.exists()) {
            return reserializeHashTable();
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(serializationFile))) {
            return (HT<String, FrequencyTable>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return reserializeHashTable();
        }
    }

    /**
     * Deserializes or reserializes the hash table from a file.
     *
     * @return The hash table.
     */
    private HT<String, FrequencyTable> reserializeHashTable() {
        HT<String, FrequencyTable> newTable = new HT<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(urlsFilePath))) {
            String url;
            while ((url = reader.readLine()) != null) {
                FrequencyTable ft = new FrequencyTable();
                ft.addWordsFromURL(url);
                newTable.put(url, ft);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        serializeHashTable(newTable);
        return newTable;
    }

    /**
     * Creates frequency tables for a list of URLs.
     *
     * @param urls The list of URLs.
     * @return A hashtable of frequency tables.
     */
    private HT<String, FrequencyTable> createFrequencyTables(ArrayList<String> urls) {
        HT<String, FrequencyTable> frequencyTables = new HT<>();
        List<Future<FrequencyTable>> futures = new ArrayList<>();
        ConcurrentHashMap<String, Future<FrequencyTable>> futureMap = new ConcurrentHashMap<>();

        for (String url : urls) {
            Callable<FrequencyTable> callable = () -> {
                FrequencyTable table = new FrequencyTable();
                table.addWordsFromURL(url);
                return table;
            };
            Future<FrequencyTable> future = executorService.submit(callable);
            futures.add(future);
            futureMap.put(url, future);
        }

        for (String url : futureMap.keySet()) {
            try {
                FrequencyTable table = futureMap.get(url).get(); // This will block until the computation is complete
                frequencyTables.put(url, table);
                System.out.println("Added FrequencyTable for URL: " + url + " with size: " + table.size());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        return frequencyTables;
    }

    /**
     * The entry point of the application.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SimilarityGUI());
    }
}