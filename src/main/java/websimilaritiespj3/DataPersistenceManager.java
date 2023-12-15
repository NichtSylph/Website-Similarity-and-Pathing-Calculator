package websimilaritiespj3;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Manages data persistence operations such as saving and loading objects and
 * URLs to and from files,
 * and extracting text content from URLs.
 * 
 * @author Joel Santos
 * @version 3.0
 * @since 11-10-2023
 */
public class DataPersistenceManager {

    // Timeout for the Jsoup connection
    private static final int TIMEOUT = 15000; // 15 seconds timeout

    /**
     * Saves the given object to a file.
     * 
     * @param object   The object to save.
     * @param filename The name of the file.
     * @throws IOException If an I/O error occurs.
     */
    public void saveObjectToFile(Serializable object, String filename) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(object);
        }
    }

    /**
     * Loads an object from a file.
     * 
     * @param filename The name of the file.
     * @return The object read from the file.
     * @throws IOException            If an I/O error occurs.
     * @throws ClassNotFoundException If the class of a serialized object cannot be
     *                                found.
     */
    public Object loadObjectFromFile(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            return ois.readObject();
        }
    }

    /**
     * Saves a collection of URLs to a text file, one URL per line.
     * 
     * @param urls     The URLs to save.
     * @param filename The name of the file.
     * @throws IOException If an I/O error occurs.
     */
    public void saveURLsToFile(Collection<String> urls, String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (String url : urls) {
                writer.write(url);
                writer.newLine();
            }
        }
    }

    /**
     * Loads URLs from a text file, where each line contains one URL.
     * 
     * @param filename The name of the file.
     * @return A list of URLs.
     * @throws IOException If an I/O error occurs.
     */
    public List<String> loadURLsFromFile(String filename) throws IOException {
        List<String> urls = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                urls.add(line);
            }
        }
        return urls;
    }

    /**
     * Extracts the text content from a URL.
     * 
     * @param url The URL to extract content from.
     * @return The text content of the URL, or an empty string if an error occurs.
     */
    public String extractContentFromURL(String url) {
        try {
            Document doc = Jsoup.connect(url).timeout(TIMEOUT).get();
            String content = doc.text();
            System.out.println("Successfully extracted content from URL " + url);
            System.out.println("Content Preview: " + content.substring(0, Math.min(content.length(), 200))); // Show a
                                                                                                             // preview
                                                                                                             // of
                                                                                                             // content
            return content;
        } catch (IOException e) {
            System.err.println("Error extracting content from URL: " + url);
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Creates a frequency table from the content of a given URL.
     *
     * @param url The URL to extract content from.
     * @return The frequency table created from the URL content.
     */
    public FrequencyTable createFrequencyTableFromURL(String url) {
        FrequencyTable frequencyTable = new FrequencyTable();
        String content = extractContentFromURL(url);
        Pattern pattern = Pattern.compile("\\W+");
        String[] words = pattern.split(content);
        for (String word : words) {
            if (word != null && !word.isEmpty()) {
                frequencyTable.addWord(word, 1);
            }
        }

        return frequencyTable;
    }
}
