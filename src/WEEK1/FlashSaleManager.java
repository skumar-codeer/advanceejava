
package week1;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class FlashSaleManager {

    // productId -> stock count
    private ConcurrentHashMap<String, AtomicInteger> inventory = new ConcurrentHashMap<>();

    // productId -> waiting list (FIFO)
    private ConcurrentHashMap<String, Queue<Integer>> waitingList = new ConcurrentHashMap<>();

    // Add product
    public void addProduct(String productId, int stock) {
        inventory.put(productId, new AtomicInteger(stock));
        waitingList.put(productId, new LinkedList<>());
    }

    // O(1) stock check
    public int checkStock(String productId) {
        return inventory.getOrDefault(productId, new AtomicInteger(0)).get();
    }

    // Thread-safe purchase
    public String purchaseItem(String productId, int userId) {

        AtomicInteger stock = inventory.get(productId);

        if (stock == null) {
            return "Product not found";
        }

        synchronized (stock) {
            if (stock.get() > 0) {
                stock.decrementAndGet();
                return "Success! Remaining stock: " + stock.get();
            } else {
                Queue<Integer> queue = waitingList.get(productId);
                queue.add(userId);
                return "Out of stock! Added to waiting list. Position: " + queue.size();
            }
        }
    }

    public static void main(String[] args) {

        FlashSaleManager manager = new FlashSaleManager();

        manager.addProduct("IPHONE15_256GB", 3);

        System.out.println("Stock: " +
                manager.checkStock("IPHONE15_256GB"));

        for (int i = 1; i <= 5; i++) {
            System.out.println(
                    manager.purchaseItem("IPHONE15_256GB", 1000 + i)
            );
        }
    }
}