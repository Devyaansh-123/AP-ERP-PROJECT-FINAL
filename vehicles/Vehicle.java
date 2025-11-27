package vehicles;

import exceptions.InvalidOperationException;

public abstract class Vehicle implements Comparable<Vehicle> {
    private final String id;
    private final String model;
    private final double maxSpeed;
    private double currentMileage;

    protected Vehicle(String id, String model, double maxSpeed, double currentMileage) throws InvalidOperationException {
        if (id == null || id.isBlank()) throw new InvalidOperationException("Vehicle ID cannot be empty");
        if (maxSpeed <= 0) throw new InvalidOperationException("Max speed must be positive");
        this.id = id;
        this.model = model;
        this.maxSpeed = maxSpeed;
        this.currentMileage = Math.max(0, currentMileage);
    }

    public String getId() { return id; }
    public String getModel() { return model; }
    public double getMaxSpeed() { return maxSpeed; }
    public double getCurrentMileage() { return currentMileage; }

    protected void addMileage(double distance) { this.currentMileage += Math.max(0, distance); }
    
    public void resetMileage() { this.currentMileage = 0; }

    public void displayInfo() {
        System.out.printf("[%s] id=%s, model=%s, maxSpeed=%.1f km/h, mileage=%.1f km%n",
                getClass().getSimpleName(), id, model, maxSpeed, currentMileage);
    }

    public abstract void move(double distance) throws InvalidOperationException, Exception;
    public abstract double calculateFuelEfficiency();

    public double estimateJourneyTime(double distance) throws InvalidOperationException {
        if (distance < 0) throw new InvalidOperationException("Distance cannot be negative");
        return distance / maxSpeed;
    }

    public abstract String toCSV();

    @Override
    public int compareTo(Vehicle other) {
        return Double.compare(other.calculateFuelEfficiency(), this.calculateFuelEfficiency());
    }
}
