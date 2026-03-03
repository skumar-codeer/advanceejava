package week2;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class ParkingLotManagement {

    enum Status { EMPTY, OCCUPIED, DELETED }

    static class ParkingSpot {
        String licensePlate;
        LocalDateTime entryTime;
        Status status;

        ParkingSpot() {
            status = Status.EMPTY;
        }
    }

    private ParkingSpot[] table;
    private int capacity;
    private int size;
    private int totalProbes;
    private Map<Integer, Integer> hourlyEntries; // hour → count

    private static final double LOAD_FACTOR_THRESHOLD = 0.7;
    private static final double HOURLY_RATE = 5.0; // $5 per hour

    public ParkingLotManagement(int capacity) {
        this.capacity = capacity;
        table = new ParkingSpot[capacity];
        for (int i = 0; i < capacity; i++)
            table[i] = new ParkingSpot();

        size = 0;
        totalProbes = 0;
        hourlyEntries = new HashMap<>();
    }

    // ===== Custom Hash Function =====
    private int hash(String licensePlate) {
        return Math.abs(licensePlate.hashCode()) % capacity;
    }

    // ===== Park Vehicle =====
    public void parkVehicle(String licensePlate) {

        if ((double) size / capacity >= LOAD_FACTOR_THRESHOLD) {
            System.out.println("Parking lot almost full!");
            return;
        }

        int index = hash(licensePlate);
        int probes = 0;

        while (table[index].status == Status.OCCUPIED) {
            index = (index + 1) % capacity; // Linear probing
            probes++;
        }

        table[index].licensePlate = licensePlate;
        table[index].entryTime = LocalDateTime.now();
        table[index].status = Status.OCCUPIED;

        size++;
        totalProbes += probes;

        int hour = LocalDateTime.now().getHour();
        hourlyEntries.put(hour, hourlyEntries.getOrDefault(hour, 0) + 1);

        System.out.println("Vehicle " + licensePlate +
                " assigned spot #" + index +
                " (" + probes + " probes)");
    }

    // ===== Exit Vehicle =====
    public void exitVehicle(String licensePlate) {

        int index = hash(licensePlate);
        int probes = 0;

        while (table[index].status != Status.EMPTY) {

            if (table[index].status == Status.OCCUPIED &&
                    table[index].licensePlate.equals(licensePlate)) {

                Duration duration =
                        Duration.between(table[index].entryTime,
                                LocalDateTime.now());

                double hours = duration.toMinutes() / 60.0;
                double fee = hours * HOURLY_RATE;

                table[index].status = Status.DELETED;
                size--;

                System.out.println("Vehicle " + licensePlate +
                        " exited from spot #" + index +
                        " | Duration: " + duration.toMinutes() +
                        " mins | Fee: $" +
                        String.format("%.2f", fee));
                return;
            }

            index = (index + 1) % capacity;
            probes++;

            if (probes >= capacity) break;
        }

        System.out.println("Vehicle not found.");
    }

    // ===== Nearest Available Spot =====
    public int findNearestAvailableSpot() {

        for (int i = 0; i < capacity; i++) {
            if (table[i].status == Status.EMPTY)
                return i;
        }
        return -1;
    }

    // ===== Statistics =====
    public void getStatistics() {

        double occupancy =
                ((double) size / capacity) * 100;

        double avgProbes =
                size == 0 ? 0 :
                        (double) totalProbes / size;

        int peakHour = -1;
        int maxEntries = 0;

        for (Map.Entry<Integer, Integer> entry :
                hourlyEntries.entrySet()) {

            if (entry.getValue() > maxEntries) {
                maxEntries = entry.getValue();
                peakHour = entry.getKey();
            }
        }

        System.out.println("\n--- Parking Statistics ---");
        System.out.println("Occupancy: "
                + String.format("%.2f", occupancy) + "%");
        System.out.println("Average Probes: "
                + String.format("%.2f", avgProbes));
        if (peakHour != -1)
            System.out.println("Peak Hour: "
                    + peakHour + ":00 - "
                    + (peakHour + 1) + ":00");
    }

    // ===== MAIN =====
    public static void main(String[] args) throws InterruptedException {

        ParkingLotManagement lot =
                new ParkingLotManagement(500);

        lot.parkVehicle("ABC-1234");
        lot.parkVehicle("ABC-1235");
        lot.parkVehicle("XYZ-9999");

        Thread.sleep(2000);

        lot.exitVehicle("ABC-1234");

        System.out.println("Nearest Spot: "
                + lot.findNearestAvailableSpot());

        lot.getStatistics();
    }
}