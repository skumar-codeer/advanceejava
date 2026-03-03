package weekX;

import java.util.*;

public class PlagiarismDetector {

    // n-gram size
    private static final int N = 5;

    // n-gram -> Set of document IDs
    private Map<String, Set<String>> invertedIndex = new HashMap<>();

    // documentId -> total n-gram count
    private Map<String, Integer> documentGramCount = new HashMap<>();


    // ===== Add Document to Database =====
    public void addDocument(String documentId, String content) {

        List<String> ngrams = generateNGrams(content);

        documentGramCount.put(documentId, ngrams.size());

        for (String gram : ngrams) {
            invertedIndex
                    .computeIfAbsent(gram, k -> new HashSet<>())
                    .add(documentId);
        }
    }


    // ===== Analyze New Document =====
    public void analyzeDocument(String documentId, String content) {

        List<String> ngrams = generateNGrams(content);

        System.out.println("Extracted " + ngrams.size() + " n-grams");

        Map<String, Integer> matchCount = new HashMap<>();

        for (String gram : ngrams) {

            if (invertedIndex.containsKey(gram)) {

                for (String matchedDoc : invertedIndex.get(gram)) {
                    matchCount.put(
                            matchedDoc,
                            matchCount.getOrDefault(matchedDoc, 0) + 1
                    );
                }
            }
        }

        for (String doc : matchCount.keySet()) {

            int matches = matchCount.get(doc);
            int totalGrams = ngrams.size();

            double similarity = (matches * 100.0) / totalGrams;

            System.out.println("Found " + matches +
                    " matching n-grams with \"" + doc + "\"");
            System.out.printf("Similarity: %.2f%%", similarity);

            if (similarity > 60) {
                System.out.println(" (PLAGIARISM DETECTED)");
            } else if (similarity > 15) {
                System.out.println(" (Suspicious)");
            } else {
                System.out.println();
            }

            System.out.println("-------------------------");
        }
    }


    // ===== Generate N-Grams =====
    private List<String> generateNGrams(String content) {

        List<String> grams = new ArrayList<>();

        String[] words = content
                .toLowerCase()
                .replaceAll("[^a-z0-9 ]", "")
                .split("\\s+");

        for (int i = 0; i <= words.length - N; i++) {

            StringBuilder gram = new StringBuilder();

            for (int j = 0; j < N; j++) {
                gram.append(words[i + j]).append(" ");
            }

            grams.add(gram.toString().trim());
        }

        return grams;
    }


    // ===== MAIN METHOD =====
    public static void main(String[] args) {

        PlagiarismDetector detector = new PlagiarismDetector();

        String essay1 = "Artificial intelligence is transforming the world. "
                + "Machine learning and deep learning are subsets of AI. "
                + "AI is used in healthcare and finance.";

        String essay2 = "Machine learning and deep learning are subsets of AI. "
                + "AI is used in healthcare and finance. "
                + "Artificial intelligence is transforming the world.";

        String essay3 = "The solar system consists of planets and stars. "
                + "Earth revolves around the sun.";

        detector.addDocument("essay_089.txt", essay1);
        detector.addDocument("essay_092.txt", essay2);
        detector.addDocument("essay_050.txt", essay3);

        System.out.println("\nAnalyzing new document:\n");

        detector.analyzeDocument("essay_123.txt", essay2);
    }
}