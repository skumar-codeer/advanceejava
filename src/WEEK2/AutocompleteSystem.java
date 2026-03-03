package week2;

import java.util.*;

public class AutocompleteSystem {

    // ===== Trie Node =====
    static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        boolean isEnd;
        List<String> topQueries = new ArrayList<>(); // Cache top results
    }

    private TrieNode root = new TrieNode();

    // Global frequency storage
    private Map<String, Integer> frequencyMap = new HashMap<>();

    private static final int TOP_K = 10;

    // ===== Insert Query =====
    public void insert(String query) {

        frequencyMap.put(query, frequencyMap.getOrDefault(query, 0) + 1);

        TrieNode current = root;

        for (char c : query.toCharArray()) {
            current = current.children.computeIfAbsent(c, k -> new TrieNode());

            updateTopQueries(current, query);
        }

        current.isEnd = true;
    }

    // ===== Update Top Queries Cache =====
    private void updateTopQueries(TrieNode node, String query) {

        if (!node.topQueries.contains(query)) {
            node.topQueries.add(query);
        }

        node.topQueries.sort((a, b) ->
                frequencyMap.get(b) - frequencyMap.get(a));

        if (node.topQueries.size() > TOP_K) {
            node.topQueries.remove(node.topQueries.size() - 1);
        }
    }

    // ===== Search Prefix =====
    public List<String> search(String prefix) {

        TrieNode current = root;

        for (char c : prefix.toCharArray()) {
            if (!current.children.containsKey(c)) {
                return handleTypo(prefix);
            }
            current = current.children.get(c);
        }

        return current.topQueries;
    }

    // ===== Basic Typo Handling (Edit Distance 1) =====
    private List<String> handleTypo(String prefix) {

        List<String> suggestions = new ArrayList<>();

        for (String query : frequencyMap.keySet()) {
            if (editDistance(prefix, query.substring(0,
                    Math.min(prefix.length(), query.length()))) == 1) {
                suggestions.add(query);
            }
        }

        suggestions.sort((a, b) ->
                frequencyMap.get(b) - frequencyMap.get(a));

        return suggestions.size() > TOP_K
                ? suggestions.subList(0, TOP_K)
                : suggestions;
    }

    // ===== Edit Distance =====
    private int editDistance(String a, String b) {

        int[][] dp = new int[a.length()+1][b.length()+1];

        for (int i=0;i<=a.length();i++)
            for (int j=0;j<=b.length();j++) {

                if (i==0) dp[i][j]=j;
                else if (j==0) dp[i][j]=i;
                else if (a.charAt(i-1)==b.charAt(j-1))
                    dp[i][j]=dp[i-1][j-1];
                else
                    dp[i][j]=1+Math.min(dp[i-1][j-1],
                            Math.min(dp[i-1][j], dp[i][j-1]));
            }

        return dp[a.length()][b.length()];
    }

    // ===== MAIN TEST =====
    public static void main(String[] args) {

        AutocompleteSystem system = new AutocompleteSystem();

        system.insert("java tutorial");
        system.insert("javascript");
        system.insert("java download");
        system.insert("java tutorial");
        system.insert("java 21 features");
        system.insert("java 21 features");

        System.out.println("Search results for 'jav':");
        List<String> results = system.search("jav");

        for (String s : results) {
            System.out.println(s + " (" +
                    system.frequencyMap.get(s) + " searches)");
        }

        System.out.println("\nUpdating frequency...");
        system.insert("java 21 features");

        System.out.println("Search results for 'java 21':");
        System.out.println(system.search("java 21"));
    }
}