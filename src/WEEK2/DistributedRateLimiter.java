package week2;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class DistributedRateLimiter {

    // ===== Token Bucket Class =====
    static class TokenBucket {

        private final int maxTokens;
        private final double refillRatePerSecond; // tokens added per second

        private double currentTokens;
        private long lastRefillTime;

        public TokenBucket(int maxTokens, double refillRatePerSecond) {
            this.maxTokens = maxTokens;
            this.refillRatePerSecond = refillRatePerSecond;
            this.currentTokens = maxTokens;
            this.lastRefillTime = System.currentTimeMillis();
        }

        // Thread-safe token check
        public synchronized boolean allowRequest() {
            refill();

            if (currentTokens >= 1) {
                currentTokens -= 1;
                return true;
            }
            return false;
        }

        // Refill tokens based on time passed
        private void refill() {
            long now = System.currentTimeMillis();
            double secondsPassed = (now - lastRefillTime) / 1000.0;

            double tokensToAdd = secondsPassed * refillRatePerSecond;

            if (tokensToAdd > 0) {
                currentTokens = Math.min(maxTokens, currentTokens + tokensToAdd);
                lastRefillTime = now;
            }
        }

        public int getRemainingTokens() {
            return (int) currentTokens;
        }

        public long getResetTimeEpoch() {
            double secondsToFull = (maxTokens - currentTokens) / refillRatePerSecond;
            return System.currentTimeMillis() + (long)(secondsToFull * 1000);
        }
    }

    // ===== Client -> TokenBucket =====
    private final ConcurrentHashMap<String, TokenBucket> clientBuckets = new ConcurrentHashMap<>();

    private static final int LIMIT_PER_HOUR = 1000;
    private static final double REFILL_RATE = LIMIT_PER_HOUR / 3600.0; // per second

    // ===== Check Rate Limit =====
    public String checkRateLimit(String clientId) {

        TokenBucket bucket = clientBuckets.computeIfAbsent(
                clientId,
                id -> new TokenBucket(LIMIT_PER_HOUR, REFILL_RATE)
        );

        boolean allowed = bucket.allowRequest();

        if (allowed) {
            return "Allowed (" + bucket.getRemainingTokens() + " requests remaining)";
        } else {
            long retryAfter = (bucket.getResetTimeEpoch() - System.currentTimeMillis()) / 1000;
            return "Denied (0 requests remaining, retry after " + retryAfter + "s)";
        }
    }

    // ===== Get Status =====
    public void getRateLimitStatus(String clientId) {

        TokenBucket bucket = clientBuckets.get(clientId);

        if (bucket == null) {
            System.out.println("Client not found.");
            return;
        }

        int used = LIMIT_PER_HOUR - bucket.getRemainingTokens();
        long resetTime = bucket.getResetTimeEpoch() / 1000;

        System.out.println("{used: " + used +
                ", limit: " + LIMIT_PER_HOUR +
                ", reset: " + resetTime + "}");
    }

    // ===== Simulate Requests =====
    public static void main(String[] args) {

        DistributedRateLimiter limiter = new DistributedRateLimiter();

        String clientId = "abc123";

        for (int i = 1; i <= 1005; i++) {
            System.out.println(limiter.checkRateLimit(clientId));
        }

        limiter.getRateLimitStatus(clientId);
    }
}