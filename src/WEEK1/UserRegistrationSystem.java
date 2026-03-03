package week1;

import java.util.*;

public class UserRegistrationSystem {

    // Stores username -> userId
    private Map<String, Integer> userDatabase = new HashMap<>();

    // Tracks username attempt frequency
    private Map<String, Integer> attemptTracker = new HashMap<>();

    // Constructor - Preloaded users
    public UserRegistrationSystem() {
        userDatabase.put("john_doe", 101);
        userDatabase.put("admin", 102);
        userDatabase.put("alice123", 103);
    }

    // O(1) username availability check
    public boolean checkAvailability(String username) {

        // Track attempts
        attemptTracker.put(username,
                attemptTracker.getOrDefault(username, 0) + 1);

        return !userDatabase.containsKey(username);
    }

    // Suggest alternatives
    public List<String> suggestAlternatives(String username) {

        List<String> suggestions = new ArrayList<>();

        for (int i = 1; i <= 3; i++) {
            String suggestion = username + i;
            if (!userDatabase.containsKey(suggestion)) {
                suggestions.add(suggestion);
            }
        }

        String modified = username.replace("_", ".");
        if (!userDatabase.containsKey(modified)) {
            suggestions.add(modified);
        }

        return suggestions;
    }

    // Get most attempted username
    public String getMostAttempted() {

        String mostAttempted = null;
        int maxAttempts = 0;

        for (Map.Entry<String, Integer> entry : attemptTracker.entrySet()) {
            if (entry.getValue() > maxAttempts) {
                maxAttempts = entry.getValue();
                mostAttempted = entry.getKey();
            }
        }

        return mostAttempted;
    }

    // Main method for testing
    public static void main(String[] args) {

        UserRegistrationSystem system =
                new UserRegistrationSystem();

        System.out.println(system.checkAvailability("john_doe"));   // false
        System.out.println(system.checkAvailability("jane_smith")); // true

        System.out.println(system.suggestAlternatives("john_doe"));

        // Simulate multiple attempts
        for (int i = 0; i < 5; i++) {
            system.checkAvailability("admin");
        }

        System.out.println("Most Attempted Username: "
                + system.getMostAttempted());
    }
}