package main;

import exceptions.InvalidOperationException;
import fleet.FleetManager;
import vehicles.*;
import simulator.HighwaySimulatorGUI;

import java.util.Optional;
import java.util.Scanner;

public class Main {
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) throws Exception {
        FleetManager fm = new FleetManager();

        // Demo data for testing
        fm.addVehicle(new Car("C001", "Toyota", 160, 9500, 4));
        fm.addVehicle(new Truck("T001", "VolvoTruck", 120, 15000, 6));
        fm.addVehicle(new Bus("B001", "CityBus", 100, 30000, 6));
        fm.addVehicle(new Airplane("A001", "Boeing737", 850, 120000, 12000));
        fm.addVehicle(new CargoShip("S001", "Maersk", 40, 200000, true));

        System.out.println("Welcome to Fleet Management System (Assignment 2/3)");
        boolean running = true;

        while (running) {
            printMenu();
            int choice = readInt("Enter choice: ");
            try {
                switch (choice) {
                    case 1 -> addVehicleCLI(fm);
                    case 2 -> removeVehicleCLI(fm);
                    case 3 -> displayAll(fm);
                    case 4 -> { fm.sortBySpeed(); System.out.println("Sorted by speed."); }
                    case 5 -> { fm.sortByModel(); System.out.println("Sorted by model."); }
                    case 6 -> { fm.sortByEfficiencyDesc(); System.out.println("Sorted by fuel efficiency (desc)."); }
                    case 7 -> saveCLI(fm);
                    case 8 -> loadCLI(fm);
                    case 9 -> fastestSlowest(fm);
                    case 10 -> report(fm);
                    case 11 -> running = false;
                    case 12 -> launchSimulator(fm);
                    default -> System.out.println("Invalid choice, try again!");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        System.out.println("Exiting Fleet Management System. Goodbye!");
    }

    private static void printMenu() {
        System.out.println("\n MENU :");
        System.out.println("1. Add Vehicle");
        System.out.println("2. Remove Vehicle");
        System.out.println("3. Display All Vehicles");
        System.out.println("4. Sort by Speed");
        System.out.println("5. Sort by Model");
        System.out.println("6. Sort by Fuel Efficiency (desc)");
        System.out.println("7. Save Fleet to File");
        System.out.println("8. Load Fleet from File");
        System.out.println("9. Show Fastest & Slowest Vehicles");
        System.out.println("10. Generate Fleet Report");
        System.out.println("11. Exit");
        System.out.println("12. Launch Highway Simulator (GUI)");
    }

    // Add vehicle
    private static void addVehicleCLI(FleetManager fm) throws Exception {
        System.out.print("Enter vehicle type (Car/Truck/Bus/Airplane/CargoShip): ");
        String type = sc.next();
        String id = readStr("Enter ID: ");
        String model = readStr("Enter Model: ");
        double maxSpeed = readDouble("Enter Max Speed (km/h): ");
        double mileage = readDouble("Enter Current Mileage (km): ");

        switch (type) {
            case "Car" -> fm.addVehicle(new Car(id, model, maxSpeed, mileage, readInt("Num Wheels: ")));
            case "Truck" -> fm.addVehicle(new Truck(id, model, maxSpeed, mileage, readInt("Num Wheels: ")));
            case "Bus" -> fm.addVehicle(new Bus(id, model, maxSpeed, mileage, readInt("Num Wheels: ")));
            case "Airplane" -> fm.addVehicle(new Airplane(id, model, maxSpeed, mileage, readDouble("Max Altitude (m): ")));
            case "CargoShip" -> fm.addVehicle(new CargoShip(id, model, maxSpeed, mileage, readBool("Has Sail? (true/false): ")));
            default -> throw new InvalidOperationException("Unknown vehicle type!");
        }

        System.out.println(type + " added successfully!");
    }

    // Remove vehicle
    private static void removeVehicleCLI(FleetManager fm) throws Exception {
        String id = readStr("Enter Vehicle ID to remove: ");
        fm.removeVehicle(id);
        System.out.println("Vehicle removed successfully!");
    }

    // Display all
    private static void displayAll(FleetManager fm) {
        if (fm.getFleet().isEmpty()) {
            System.out.println("No vehicles in the fleet.");
            return;
        }
        fm.getFleet().forEach(Vehicle::displayInfo);
        System.out.println("Distinct Models: " + fm.getDistinctModels());
        System.out.println("Sorted Models:   " + fm.getSortedModels());
    }

    // Save fleet
    private static void saveCLI(FleetManager fm) {
        String file = readStr("Enter filename to save (e.g., fleetdata.csv): ");
        fm.saveToFile(file);
    }

    // Load fleet
    private static void loadCLI(FleetManager fm) {
        String file = readStr("Enter filename to load: ");
        fm.loadFromFile(file);
    }

    // Show fastest and slowest
    private static void fastestSlowest(FleetManager fm) {
        Optional<Vehicle> fastest = fm.getFastest();
        Optional<Vehicle> slowest = fm.getSlowest();
        fastest.ifPresent(v -> { System.out.print("Fastest -> "); v.displayInfo(); });
        slowest.ifPresent(v -> { System.out.print("Slowest -> "); v.displayInfo(); });
    }

    // Generate report
    private static void report(FleetManager fm) {
        System.out.println(fm.generateReport());
    }

    // input
    private static int readInt(String prompt) { System.out.print(prompt); return sc.nextInt(); }
    private static double readDouble(String prompt) { System.out.print(prompt); return sc.nextDouble(); }
    private static String readStr(String prompt) { System.out.print(prompt); return sc.next(); }
    private static boolean readBool(String prompt) { System.out.print(prompt); return sc.nextBoolean(); }

    private static void launchSimulator(FleetManager fm) {
        javax.swing.SwingUtilities.invokeLater(() -> new HighwaySimulatorGUI(fm.getFleet()).setVisible(true));
    }
}
