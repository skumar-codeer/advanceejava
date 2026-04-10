import java.util.*;

public class Main2 {

    // ✅ Client Class
    static class Client {
        String name;
        int riskScore;
        double accountBalance;

        public Client(String name, int riskScore, double accountBalance) {
            this.name = name;
            this.riskScore = riskScore;
            this.accountBalance = accountBalance;
        }

        @Override
        public String toString() {
            return name + ":" + riskScore + " (Bal:" + accountBalance + ")";
        }
    }

    // ✅ Bubble Sort (Ascending Risk Score)
    public static void bubbleSort(Client[] arr) {
        int n = arr.length;
        int swaps = 0;

        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;

            for (int j = 0; j < n - i - 1; j++) {
                if (arr[j].riskScore > arr[j + 1].riskScore) {
                    // swap
                    Client temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;

                    swaps++;
                    swapped = true;

                    // 🔹 visualize swaps
                    System.out.println("Swap: " + arr[j] + " <-> " + arr[j + 1]);
                }
            }

            if (!swapped) break; // early stop
        }

        System.out.println("\nBubble Sort (Ascending): " + Arrays.toString(arr));
        System.out.println("Total Swaps: " + swaps);
    }

    // ✅ Insertion Sort (DESC riskScore + accountBalance)
    public static void insertionSort(Client[] arr) {
        for (int i = 1; i < arr.length; i++) {
            Client key = arr[i];
            int j = i - 1;

            while (j >= 0 && compare(arr[j], key) < 0) {
                arr[j + 1] = arr[j]; // shift right
                j--;
            }

            arr[j + 1] = key;
        }

        System.out.println("\nInsertion Sort (DESC risk + balance): " + Arrays.toString(arr));
    }

    // ✅ Compare for DESC riskScore, then DESC balance
    private static int compare(Client c1, Client c2) {
        if (c1.riskScore != c2.riskScore) {
            return Integer.compare(c1.riskScore, c2.riskScore);
        }
        return Double.compare(c1.accountBalance, c2.accountBalance);
    }

    // ✅ Top N High Risk Clients
    public static void topHighRisk(Client[] arr, int topN) {
        System.out.print("\nTop " + topN + " High Risk Clients: ");

        for (int i = 0; i < Math.min(topN, arr.length); i++) {
            System.out.print(arr[i].name + "(" + arr[i].riskScore + ") ");
        }

        System.out.println();
    }

    // ✅ Main Method
    public static void main(String[] args) {

        Client[] clients = {
            new Client("clientC", 80, 5000),
            new Client("clientA", 20, 2000),
            new Client("clientB", 50, 3000),
            new Client("clientD", 90, 1000),
            new Client("clientE", 70, 4000)
        };

        // 🔹 Bubble Sort
        Client[] bubbleArr = clients.clone();
        bubbleSort(bubbleArr);

        // 🔹 Insertion Sort
        Client[] insertionArr = clients.clone();
        insertionSort(insertionArr);

        // 🔹 Top 3 (change to 10 for real case)
        topHighRisk(insertionArr, 3);
    }
}