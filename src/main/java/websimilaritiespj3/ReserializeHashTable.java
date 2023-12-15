package websimilaritiespj3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * This class is responsible for re-serializing a hash table with updated
 * frequency tables.
 * It reads URLs from a file, processes each to create a frequency table, and
 * then serializes the hash table.
 * 
 * @author Joel Santos
 * @version 3.0
 * @since 11-10-2023
 */
public class ReserializeHashTable {

    private String filePath;
    private DataPersistenceManager dataPersistenceManager;

    /**
     * Constructs a new instance of ReserializeHashTable.
     *
     * @param filePath The path to the file containing the list of URLs to be
     *                 processed.
     */
    public ReserializeHashTable(String filePath) {
        this.filePath = filePath;
        this.dataPersistenceManager = new DataPersistenceManager();
    }

    /**
     * Reads URLs from a file, creates a frequency table for each, and serializes
     * the hashtable.
     * If an I/O error occurs during reading or serialization, the error is printed
     * and the method exits.
     */
    public void reserialize() {
        HT<String, FrequencyTable> hashTable = new HT<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(this.filePath))) {
            String url;
            while ((url = reader.readLine()) != null) {
                FrequencyTable frequencyTable = createFrequencyTableForUrl(url);
                hashTable.put(url, frequencyTable);
            }
        } catch (IOException e) {
            System.err.println("An error occurred while reading URLs from file.");
            e.printStackTrace();
            return;
        }

        // Specify the output file path in the src/main/resources folder
        String outputFilePath = Paths.get("src", "main", "resources", "hashTable.ser").toString();

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(outputFilePath))) {
            oos.writeObject(hashTable);
        } catch (IOException e) {
            System.err.println("An error occurred during serialization of the hash table.");
            e.printStackTrace();
        }
    }

    /**
     * Creates a frequency table for a given URL.
     *
     * @param url The URL for which to create the frequency table.
     * @return A frequency table representing the word frequencies in the content of
     *         the URL.
     */
    private FrequencyTable createFrequencyTableForUrl(String url) {
        FrequencyTable frequencyTable = new FrequencyTable();
        String content = dataPersistenceManager.extractContentFromURL(url);
        String[] words = content.split("\\s+");
        for (String word : words) {
            if (word != null && !word.isEmpty()) {
                frequencyTable.addWord(word, 1);
            }
        }
        return frequencyTable;
    }
}
