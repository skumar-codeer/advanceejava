package week2;

import java.util.*;

public class MultiLevelCacheSystem {

    static class Video {
        String videoId;
        String content;

        public Video(String videoId, String content) {
            this.videoId = videoId;
            this.content = content;
        }
    }

    // ==============================
    // L1 CACHE (Memory)
    // ==============================
    private final int L1_CAPACITY = 10000;
    private LinkedHashMap<String, Video> l1Cache =
            new LinkedHashMap<>(L1_CAPACITY, 0.75f, true) {
                protected boolean removeEldestEntry(Map.Entry<String, Video> eldest) {
                    return size() > L1_CAPACITY;
                }
            };

    // ==============================
    // L2 CACHE (SSD Simulated)
    // ==============================
    private final int L2_CAPACITY = 100000;
    private LinkedHashMap<String, Video> l2Cache =
            new LinkedHashMap<>(L2_CAPACITY, 0.75f, true) {
                protected boolean removeEldestEntry(Map.Entry<String, Video> eldest) {
                    return size() > L2_CAPACITY;
                }
            };

    // ==============================
    // L3 DATABASE (All Videos)
    // ==============================
    private Map<String, Video> database = new HashMap<>();

    // ==============================
    // Statistics
    // ==============================
    private int l1Hits = 0, l2Hits = 0, l3Hits = 0;
    private int totalRequests = 0;

    // ==============================
    // Get Video
    // ==============================
    public Video getVideo(String videoId) {

        totalRequests++;
        long start = System.nanoTime();

        // ---- L1 Check ----
        if (l1Cache.containsKey(videoId)) {
            l1Hits++;
            simulateDelay(0.5);
            System.out.println("L1 HIT (0.5ms)");
            return l1Cache.get(videoId);
        }

        System.out.println("L1 MISS");

        // ---- L2 Check ----
        if (l2Cache.containsKey(videoId)) {
            l2Hits++;
            simulateDelay(5);
            System.out.println("L2 HIT (5ms) → Promoted to L1");

            Video v = l2Cache.get(videoId);
            l1Cache.put(videoId, v); // Promote
            return v;
        }

        System.out.println("L2 MISS");

        // ---- L3 DB ----
        if (database.containsKey(videoId)) {
            l3Hits++;
            simulateDelay(150);
            System.out.println("L3 DB HIT (150ms) → Added to L2");

            Video v = database.get(videoId);
            l2Cache.put(videoId, v); // Add to L2
            return v;
        }

        System.out.println("Video Not Found");
        return null;
    }

    // ==============================
    // Add Video to Database
    // ==============================
    public void addToDatabase(String videoId, String content) {
        database.put(videoId, new Video(videoId, content));
    }

    // ==============================
    // Cache Invalidation
    // ==============================
    public void invalidate(String videoId) {
        l1Cache.remove(videoId);
        l2Cache.remove(videoId);
        database.remove(videoId);
        System.out.println("Video invalidated from all levels.");
    }

    // ==============================
    // Statistics
    // ==============================
    public void getStatistics() {

        double l1Rate = percentage(l1Hits);
        double l2Rate = percentage(l2Hits);
        double l3Rate = percentage(l3Hits);

        System.out.println("\n---- CACHE STATISTICS ----");
        System.out.println("L1 Hit Rate: " + l1Rate + "%");
        System.out.println("L2 Hit Rate: " + l2Rate + "%");
        System.out.println("L3 Hit Rate: " + l3Rate + "%");

        double overall = percentage(l1Hits + l2Hits);
        System.out.println("Overall Cache Hit Rate: " + overall + "%");
    }

    private double percentage(int hits) {
        return totalRequests == 0 ? 0 :
                (hits * 100.0) / totalRequests;
    }

    // Simulate latency
    private void simulateDelay(double ms) {
        try {
            Thread.sleep((long) ms);
        } catch (InterruptedException ignored) {}
    }

    // ==============================
    // MAIN
    // ==============================
    public static void main(String[] args) {

        MultiLevelCacheSystem cache =
                new MultiLevelCacheSystem();

        // Add videos to DB
        cache.addToDatabase("video_123", "Action Movie");
        cache.addToDatabase("video_999", "Drama Movie");

        System.out.println("Request 1:");
        cache.getVideo("video_123");

        System.out.println("\nRequest 2:");
        cache.getVideo("video_123");

        System.out.println("\nRequest 3:");
        cache.getVideo("video_999");

        System.out.println("\nRequest 4:");
        cache.getVideo("video_999");

        cache.getStatistics();
    }
}