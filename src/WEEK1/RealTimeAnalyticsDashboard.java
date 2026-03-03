package weekX;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class RealTimeAnalyticsDashboard {

    // pageUrl -> total visits
    private ConcurrentHashMap<String, AtomicInteger> pageViews = new ConcurrentHashMap<>();

    // pageUrl -> unique visitors
    private ConcurrentHashMap<String, Set<String>> uniqueVisitors = new ConcurrentHashMap<>();

    // source -> count
    private ConcurrentHashMap<String, AtomicInteger> trafficSources = new ConcurrentHashMap<>();

    // ===== Process Incoming Event =====
    public void processEvent(String url, String userId, String source) {

        // Count page views
        pageViews.computeIfAbsent(url, k -> new AtomicInteger(0)).incrementAndGet();

        // Track unique visitors
        uniqueVisitors
                .computeIfAbsent(url, k -> ConcurrentHashMap.newKeySet())
                .add(userId);

        // Count traffic source
        trafficSources
                .computeIfAbsent(source, k -> new AtomicInteger(0))
                .incrementAndGet();
    }

    // ===== Get Dashboard =====
    public void getDashboard() {

        System.out.println("\n========== DASHBOARD ==========");

        // Top 10 pages
        PriorityQueue<Map.Entry<String, AtomicInteger>> pq =
                new PriorityQueue<>(
                        (a, b) -> b.getValue().get() - a.getValue().get()
                );

        pq.addAll(pageViews.entrySet());

        System.out.println("\nTop Pages:");
        int rank = 1;

        while (!pq.isEmpty() && rank <= 10) {

            Map.Entry<String, AtomicInteger> entry = pq.poll();
            String url = entry.getKey();
            int views = entry.getValue().get();
            int unique = uniqueVisitors.getOrDefault(url, Collections.emptySet()).size();

            System.out.println(rank + ". " + url +
                    " - " + views + " views (" + unique + " unique)");

            rank++;
        }

        // Traffic Source %
        System.out.println("\nTraffic Sources:");

        int totalTraffic = trafficSources.values()
                .stream()
                .mapToInt(AtomicInteger::get)
                .sum();

        for (String source : trafficSources.keySet()) {

            int count = trafficSources.get(source).get();
            double percent = (count * 100.0) / totalTraffic;

            System.out.printf("%s: %.2f%%\n", source, percent);
        }

        System.out.println("=================================\n");
    }

    // ===== Auto Dashboard Refresh Every 5 Seconds =====
    public void startAutoRefresh() {

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(() -> {
            getDashboard();
        }, 5, 5, TimeUnit.SECONDS);
    }

    // ===== Simulate Traffic =====
    public static void main(String[] args) {

        RealTimeAnalyticsDashboard dashboard = new RealTimeAnalyticsDashboard();

        dashboard.startAutoRefresh();

        String[] pages = {
                "/article/breaking-news",
                "/sports/championship",
                "/tech/ai-update",
                "/world/politics"
        };

        String[] sources = {"Google", "Facebook", "Direct", "Twitter"};

        Random random = new Random();

        // Simulate 1M events/hour (~277 per second)
        while (true) {

            String page = pages[random.nextInt(pages.length)];
            String user = "user_" + random.nextInt(10000);
            String source = sources[random.nextInt(sources.length)];

            dashboard.processEvent(page, user, source);

            try {
                Thread.sleep(10); // simulate traffic speed
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}