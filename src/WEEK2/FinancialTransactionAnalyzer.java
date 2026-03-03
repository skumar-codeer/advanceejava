package week2;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class FinancialTransactionAnalyzer {

    static class Transaction {
        int id;
        double amount;
        String merchant;
        String accountId;
        LocalDateTime time;

        public Transaction(int id, double amount, String merchant,
                           String accountId, LocalDateTime time) {
            this.id = id;
            this.amount = amount;
            this.merchant = merchant;
            this.accountId = accountId;
            this.time = time;
        }

        @Override
        public String toString() {
            return "ID:" + id + " Amount:" + amount;
        }
    }

    private List<Transaction> transactions;

    public FinancialTransactionAnalyzer(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    // =========================
    // 1️⃣ Classic Two-Sum
    // =========================
    public List<List<Transaction>> findTwoSum(double target) {

        Map<Double, Transaction> map = new HashMap<>();
        List<List<Transaction>> result = new ArrayList<>();

        for (Transaction t : transactions) {

            double complement = target - t.amount;

            if (map.containsKey(complement)) {
                result.add(Arrays.asList(map.get(complement), t));
            }

            map.put(t.amount, t);
        }

        return result;
    }

    // =========================
    // 2️⃣ Two-Sum within 1 Hour
    // =========================
    public List<List<Transaction>> findTwoSumWithTimeWindow(double target) {

        List<List<Transaction>> result = new ArrayList<>();

        transactions.sort(Comparator.comparing(t -> t.time));

        for (int i = 0; i < transactions.size(); i++) {

            Map<Double, Transaction> map = new HashMap<>();

            for (int j = i; j < transactions.size(); j++) {

                Duration diff = Duration.between(
                        transactions.get(i).time,
                        transactions.get(j).time);

                if (diff.toHours() > 1)
                    break;

                double complement =
                        target - transactions.get(j).amount;

                if (map.containsKey(complement)) {
                    result.add(Arrays.asList(
                            map.get(complement),
                            transactions.get(j)));
                }

                map.put(transactions.get(j).amount,
                        transactions.get(j));
            }
        }

        return result;
    }

    // =========================
    // 3️⃣ Duplicate Detection
    // =========================
    public List<String> detectDuplicates() {

        Map<String, Set<String>> map = new HashMap<>();
        List<String> duplicates = new ArrayList<>();

        for (Transaction t : transactions) {

            String key = t.amount + "_" + t.merchant;

            map.putIfAbsent(key, new HashSet<>());
            map.get(key).add(t.accountId);
        }

        for (Map.Entry<String, Set<String>> entry : map.entrySet()) {

            if (entry.getValue().size() > 1) {
                duplicates.add("Duplicate detected: "
                        + entry.getKey()
                        + " Accounts: "
                        + entry.getValue());
            }
        }

        return duplicates;
    }

    // =========================
    // 4️⃣ K-Sum (Generalized)
    // =========================
    public List<List<Transaction>> findKSum(int k, double target) {

        List<List<Transaction>> result = new ArrayList<>();

        transactions.sort(Comparator.comparingDouble(t -> t.amount));

        kSumHelper(0, k, target,
                new ArrayList<>(), result);

        return result;
    }

    private void kSumHelper(int start, int k,
                            double target,
                            List<Transaction> current,
                            List<List<Transaction>> result) {

        if (k == 0 && target == 0) {
            result.add(new ArrayList<>(current));
            return;
        }

        if (k == 0) return;

        for (int i = start; i < transactions.size(); i++) {

            if (transactions.get(i).amount > target)
                break;

            current.add(transactions.get(i));

            kSumHelper(i + 1, k - 1,
                    target - transactions.get(i).amount,
                    current, result);

            current.remove(current.size() - 1);
        }
    }

    // =========================
    // MAIN
    // =========================
    public static void main(String[] args) {

        List<Transaction> list = new ArrayList<>();

        list.add(new Transaction(1, 500, "Store A",
                "acc1",
                LocalDateTime.now()));

        list.add(new Transaction(2, 300, "Store B",
                "acc2",
                LocalDateTime.now().plusMinutes(15)));

        list.add(new Transaction(3, 200, "Store C",
                "acc3",
                LocalDateTime.now().plusMinutes(30)));

        list.add(new Transaction(4, 500, "Store A",
                "acc4",
                LocalDateTime.now().plusMinutes(45)));

        FinancialTransactionAnalyzer analyzer =
                new FinancialTransactionAnalyzer(list);

        System.out.println("Two Sum (500): "
                + analyzer.findTwoSum(500));

        System.out.println("Two Sum with 1hr window (500): "
                + analyzer.findTwoSumWithTimeWindow(500));

        System.out.println("Duplicates: "
                + analyzer.detectDuplicates());

        System.out.println("K=3 Sum (1000): "
                + analyzer.findKSum(3, 1000));
    }
}