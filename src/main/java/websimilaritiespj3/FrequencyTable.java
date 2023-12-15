package websimilaritiespj3;

import java.io.Serializable;
import java.util.Set;

/**
 * A frequency table for counting the occurrences of words.
 * It provides methods for adding words, merging with other frequency tables,
 * and performing operations on the frequencies.
 * 
 * @author Joel Santos
 * @version 3.0
 * @since 11-10-2023
 */
public class FrequencyTable implements Serializable {
    private static final long serialVersionUID = 1L;
    private transient DataPersistenceManager dataPersistenceManager;
    private HT<String, Integer> table;

    /**
     * Constructs an empty FrequencyTable.
     */
    public FrequencyTable() {
        table = new HT<>();
        dataPersistenceManager = new DataPersistenceManager();
    }

    /**
     * Adds a word to the frequency table with the specified frequency.
     * 
     * @param word      The word to add.
     * @param frequency The frequency of the word.
     */
    public void addWord(String word, int frequency) {
        word = word.toLowerCase().replaceAll("[^a-zA-Z0-9]", "");
        if (!word.isEmpty()) {
            table.put(word, table.getOrDefault(word, 0) + frequency);
        }
    }

    /**
     * Gets the frequency of a specific word in the table.
     * 
     * @param word The word to look up.
     * @return The frequency of the word.
     */
    public int getFrequency(String word) {
        return table.getOrDefault(word.toLowerCase(), 0);
    }

    /**
     * Merges this frequency table with another frequency table.
     * 
     * @param other The other frequency table to merge with.
     */
    public void merge(FrequencyTable other) {
        Set<String> otherKeys = other.keySet();
        for (String word : otherKeys) {
            int frequency = this.getFrequency(word);
            int otherFrequency = other.getFrequency(word);
            this.table.put(word, frequency + otherFrequency);
        }
    }

    /**
     * Divides all frequencies in the table by a specified divisor.
     * 
     * @param divisor The divisor by which to divide the frequencies.
     * @throws IllegalArgumentException If the divisor is zero.
     */
    public void divide(int divisor) {
        if (divisor == 0) {
            throw new IllegalArgumentException("Divisor cannot be zero.");
        }

        Set<String> keys = table.keySet();
        for (String word : keys) {
            int currentFrequency = table.get(word);
            table.put(word, currentFrequency / divisor); // Assuming integer division
        }
    }

    /**
     * Adds words to the frequency table from the content extracted from a specified
     * URL.
     * 
     * @param url The URL from which to extract and add words.
     */
    public void addWordsFromURL(String url) {
        System.out.println("Attempting to add words from URL: " + url);
        try {
            String content = dataPersistenceManager.extractContentFromURL(url);
            if (content.isEmpty()) {
                System.err.println("No content extracted from URL: " + url);
                return;
            }
            System.out.println("Extracted content length: " + content.length());

            String[] words = content.split("\\s+");
            System.out.println("Extracted " + words.length + " words from URL: " + url);

            for (String word : words) {
                if (word != null && !word.isEmpty()) {
                    addWord(word, 1);
                }
            }
        } catch (Exception e) {
            System.err.println("Error processing URL: " + url);
            e.printStackTrace();
        }
    }

    /**
     * Returns the total count of all words in the frequency table.
     * 
     * @return The total word count.
     */
    public int getTotalWordCount() {
        int total = 0;
        for (Integer frequency : table.values()) {
            total += frequency;
        }
        return total;
    }

    /**
     * Returns the number of unique words in the frequency table.
     * 
     * @return The size of the frequency table.
     */
    public int size() {
        return table.size();
    }

    /**
     * Returns a Set view of the keys contained in this frequency table.
     * 
     * @return A set view of the keys.
     */
    public Set<String> keySet() {
        return table.keySet();
    }

    /**
     * Prints the contents of the frequency table to the console. Mainly for
     * debugging purposes.
     */
    public void printContents() {
        System.out.println("Frequency Table Contents:");
        for (HT.Node<String, Integer> node : table.entrySet()) {
            System.out.println(node.key + ": " + node.value);
        }
    }
}
