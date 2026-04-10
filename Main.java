import java.util.*;

public class Main {

    // ✅ Transaction Class
    static class Transaction {
        String id;
        double fee;
        String timestamp; // HH:MM

        public Transaction(String id, double fee, String timestamp) {
            this.id = id;
            this.fee = fee;
            this.timestamp = timestamp;
        }

        @Override
        public String toString() {
            return id + ":" + fee + "@" + timestamp;
        }
    }

    // ✅ Bubble Sort (by fee)
    public static void bubbleSort(List<Transaction> list) {
        int n = list.size();
        boolean swapped;
        int passes = 0, swaps = 0;

        for (int i = 0; i < n - 1; i++) {
            swapped = false;
            passes++;

            for (int j = 0; j < n - i - 1; j++) {
                if (list.get(j).fee > list.get(j + 1).fee) {
                    // swap
                    Transaction temp = list.get(j);
                    list.set(j, list.get(j + 1));
                    list.set(j + 1, temp);

                    swapped = true;
                    swaps++;
                }
            }

            if (!swapped) break; // early stop
        }

        System.out.println("Bubble Sort (by fee): " + list);
        System.out.println("Passes: " + passes + ", Swaps: " + swaps);
    }

    // ✅ Insertion Sort (fee + timestamp)
    public static void insertionSort(List<Transaction> list) {
        for (int i = 1; i < list.size(); i++) {
            Transaction key = list.get(i);
            int j = i - 1;

            while (j >= 0 && compare(list.get(j), key) > 0) {
                list.set(j + 1, list.get(j)); // shift right
                j--;
            }

            list.set(j + 1, key);
        }

        System.out.println("Insertion Sort (fee + timestamp): " + list);
    }

    // ✅ Compare by fee, then timestamp
    private static int compare(Transaction t1, Transaction t2) {
        if (t1.fee != t2.fee) {
            return Double.compare(t1.fee, t2.fee);
        }
        return t1.timestamp.compareTo(t2.timestamp);
    }

    // ✅ Outlier Detection (>50)
    public static void findOutliers(List<Transaction> list) {
        System.out.print("High-fee outliers (>50): ");
        boolean found = false;

        for (Transaction t : list) {
            if (t.fee > 50) {
                System.out.print(t + " ");
                found = true;
            }
        }

        if (!found) {
            System.out.print("None");
        }
        System.out.println();
    }

    // ✅ Main Method
    public static void main(String[] args) {

        List<Transaction> transactions = new ArrayList<>();

        // Sample Data
        transactions.add(new Transaction("id1", 10.5, "10:00"));
        transactions.add(new Transaction("id2", 25.0, "09:30"));
        transactions.add(new Transaction("id3", 5.0, "10:15"));
        transactions.add(new Transaction("id4", 60.0, "11:00")); // outlier

        // 🔹 Bubble Sort
        List<Transaction> bubbleList = new ArrayList<>(transactions);
        bubbleSort(bubbleList);

        System.out.println();

        // 🔹 Insertion Sort
        List<Transaction> insertionList = new ArrayList<>(transactions);
        insertionSort(insertionList);

        System.out.println();

        // 🔹 Outliers
        findOutliers(transactions);
    }
}