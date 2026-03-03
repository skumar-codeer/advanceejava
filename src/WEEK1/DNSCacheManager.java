
package week3;

import java.util.*;
import java.util.concurrent.*;

public class DNSCacheManager {

    // ===== Entry Class =====
    static class DNSEntry {
        String domain;
        String ipAddress;
        long expiryTime;

        DNSEntry(String domain, String ipAddress, long ttlSeconds) {
            this.domain = domain;
            this.ipAddress = ipAddress;
            this.expiryTime = System.currentTimeMillis() + (ttlSeconds * 1000);
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
    }

    // ===== LRU Cache using LinkedHashMap =====
    private final int maxSize;

    private final Map<String, DNSEntry> cache;

    // Stats
    private long hits = 0;
    private long misses = 0;

    public DNSCacheManager(int maxSize) {
        this.maxSize = maxSize;

        this.cache = new LinkedHashMap<>(16, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<String, DNSEntry> eldest) {
                return size() > DNSCacheManager.this.maxSize;
            }
        };

        startCleanupThread();
    }

    // ===== Resolve Method =====
    public synchronized String resolve(String domain) {

        long startTime = System.nanoTime();

        DNSEntry entry = cache.get(domain);

        if (entry != null && !entry.isExpired()) {
            hits++;
            long endTime = System.nanoTime();
            System.out.println("Cache HIT (" + ((endTime - startTime) / 1_000_000.0) + " ms)");
            return entry.ipAddress;
        }

        misses++;

        if (entry != null && entry.isExpired()) {
            cache.remove(domain);
            System.out.println("Cache EXPIRED");
        } else {
            System.out.println("Cache MISS");
        }

        // Simulate upstream DNS query (100ms delay)
        String newIP = queryUpstreamDNS(domain);

        cache.put(domain, new DNSEntry(domain, newIP, 5)); // TTL 5 sec

        return newIP;
    }

    // ===== Simulated Upstream DNS =====
    private String queryUpstreamDNS(String domain) {
        try {
            Thread.sleep(100); // simulate network delay
        } catch (InterruptedException ignored) {}

        return "172.217." + new Random().nextInt(255) + "." + new Random().nextInt(255);
    }

    // ===== Cleanup Thread =====
    private void startCleanupThread() {
        Thread cleaner = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(5000);
                    synchronized (this) {
                        Iterator<Map.Entry<String, DNSEntry>> iterator = cache.entrySet().iterator();
                        while (iterator.hasNext()) {
                            if (iterator.next().getValue().isExpired()) {
                                iterator.remove();
                            }
                        }
                    }
                } catch (InterruptedException ignored) {}
            }
        });

        cleaner.setDaemon(true);
        cleaner.start();
    }

    // ===== Cache Stats =====
    public void getCacheStats() {
        long total = hits + misses;
        double hitRate = total == 0 ? 0 : (hits * 100.0 / total);

        System.out.println("Total Requests: " + total);
        System.out.println("Hits: " + hits);
        System.out.println("Misses: " + misses);
        System.out.println("Hit Rate: " + hitRate + "%");
    }

    // ===== Main for Testing =====
    public static void main(String[] args) throws InterruptedException {

        DNSCacheManager cache = new DNSCacheManager(3);

        System.out.println("IP: " + cache.resolve("google.com"));
        System.out.println("IP: " + cache.resolve("google.com"));

        Thread.sleep(6000); // wait for TTL expiry

        System.out.println("IP: " + cache.resolve("google.com"));

        cache.getCacheStats();
    }
}