package websimilaritiespj3;

/**
 * Provides methods to calculate the cosine similarity between two frequency
 * tables.
 * 
 * @author Joel Santos
 * @version 3.0
 * @since 11-10-2023
 */
public class SimilarityMetricCalculator {

    /**
     * Calculates the cosine similarity between two FrequencyTable objects.
     * 
     * @param table1 The first frequency table.
     * @param table2 The second frequency table.
     * @return The cosine similarity value between the two frequency tables.
     * @throws IllegalArgumentException if either frequency table is null.
     */
    public static double calculateCosineSimilarity(FrequencyTable table1, FrequencyTable table2) {
        if (table1 == null || table2 == null) {
            throw new IllegalArgumentException("Frequency tables cannot be null.");
        }

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        // Calculate the dot product and normA
        for (String word : table1.keySet()) {
            int freqA = table1.getFrequency(word);
            normA += Math.pow(freqA, 2);

            int freqB = table2.getFrequency(word);
            dotProduct += freqA * freqB; // freqB will be 0 if the word is not found in table2
        }

        // Calculate normB
        for (String word : table2.keySet()) {
            int freqB = table2.getFrequency(word);
            normB += Math.pow(freqB, 2);
        }

        // Debug: Print the sizes of the tables and the calculated similarity
        System.out.println("Table 1 size: " + table1.size() + ", Table 2 size: " + table2.size());
        double similarity = dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
        System.out.println("Similarity: " + similarity);

        return similarity;
    }
}
