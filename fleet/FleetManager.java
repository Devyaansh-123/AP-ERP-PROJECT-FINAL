package fleet;

import exceptions.InvalidOperationException;
import vehicles.Vehicle;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class FleetManager {
    private final List<Vehicle> fleet = new ArrayList<>();
    private final Set<String> modelNames = new HashSet<>();
    private final TreeSet<String> sortedModels = new TreeSet<>();

    // Add a vehicle
    public void addVehicle(Vehicle v) throws InvalidOperationException {
        Objects.requireNonNull(v);
        if (fleet.stream().anyMatch(x -> x.getId().equals(v.getId())))
            throw new InvalidOperationException("Duplicate vehicle ID: " + v.getId());
        fleet.add(v);
        modelNames.add(v.getModel());
        sortedModels.add(v.getModel());
    }

    // Remove a vehicle
    public void removeVehicle(String id) throws InvalidOperationException {
        boolean removed = fleet.removeIf(v -> v.getId().equals(id));
        if (!removed) throw new InvalidOperationException("Vehicle not found: " + id);
        refreshModelSets();
    }

    private void refreshModelSets() {
        modelNames.clear();
        sortedModels.clear();
        for (Vehicle v : fleet) {
            modelNames.add(v.getModel());
            sortedModels.add(v.getModel());
        }
    }

    // Getters
    public List<Vehicle> getFleet() { return Collections.unmodifiableList(fleet); }
    public Set<String> getDistinctModels() { return Collections.unmodifiableSet(modelNames); }
    public SortedSet<String> getSortedModels() { return Collections.unmodifiableSortedSet(sortedModels); }

    // Sorting
    public void sortBySpeed() { fleet.sort(Comparator.comparingDouble(Vehicle::getMaxSpeed)); }
    public void sortByModel() { fleet.sort(Comparator.comparing(Vehicle::getModel)); }
    public void sortByEfficiencyDesc() { Collections.sort(fleet); }

    // Fastest/Slowest
    public Optional<Vehicle> getFastest() {
        return fleet.stream().max(Comparator.comparingDouble(Vehicle::getMaxSpeed));
    }
    public Optional<Vehicle> getSlowest() {
        return fleet.stream().min(Comparator.comparingDouble(Vehicle::getMaxSpeed));
    }

    // Report
    public String generateReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Fleet Report ===\n");
        sb.append("Total vehicles: ").append(fleet.size()).append('\n');
        Map<String, Long> byType = fleet.stream()
                .collect(Collectors.groupingBy(v -> v.getClass().getSimpleName(), Collectors.counting()));
        byType.forEach((k, v) -> sb.append(String.format("%s: %d\n", k, v)));
        double avgEff = fleet.isEmpty() ? 0 :
                fleet.stream().mapToDouble(Vehicle::calculateFuelEfficiency).average().orElse(0);
        double totalMileage = fleet.stream().mapToDouble(Vehicle::getCurrentMileage).sum();
        sb.append(String.format("Average efficiency: %.2f km/l\n", avgEff));
        sb.append(String.format("Total mileage: %.1f km\n", totalMileage));
        sb.append("Distinct models: ").append(modelNames.size())
                .append(" -> ").append(sortedModels).append('\n');
        return sb.toString();
    }

    // Save to CSV
    public void saveToFile(String filename) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (Vehicle v : fleet) {
                bw.write(v.toCSV());
                bw.newLine();
            }
            System.out.println("Fleet saved to file: " + filename);
        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }

    // Load from CSV
    public void loadFromFile(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                try {
                    Vehicle v = VehicleFactory.createFromCSV(line);
                    addVehicle(v);
                } catch (Exception ex) {
                    System.out.println("Skipping malformed line: " + line + " -> " + ex.getMessage());
                }
            }
            System.out.println("Fleet loaded from file: " + filename);
        } catch (IOException e) {
            System.out.println("Error loading file: " + e.getMessage());
        }
    }
}
