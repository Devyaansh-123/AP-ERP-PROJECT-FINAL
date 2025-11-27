package vehicles;

import exceptions.InsufficientFuelException;
import exceptions.InvalidOperationException;
import exceptions.OverloadException;
import interfaces.CargoCarrier;
import interfaces.FuelConsumable;
import interfaces.Maintainable;

public class Truck extends LandVehicle implements FuelConsumable, CargoCarrier, Maintainable {
    private double fuelLevel = 0;
    private final double cargoCapacity = 5000; // kg
    private double currentCargo = 0;
    private boolean maintenanceNeeded = false;

    public Truck(String id, String model, double maxSpeed, double currentMileage, int numWheels)
            throws InvalidOperationException {
        super(id, model, maxSpeed, currentMileage, numWheels);
    }

    @Override
    public void move(double distance) throws InvalidOperationException, InsufficientFuelException {
        if (distance < 0) throw new InvalidOperationException("Distance cannot be negative");
        double eff = calculateFuelEfficiency();
        if (currentCargo > 0.5 * cargoCapacity) eff *= 0.9; // reduce efficiency
        double needed = distance / eff;
        if (needed > fuelLevel) throw new InsufficientFuelException("Not enough fuel in truck " + getId());
        fuelLevel -= needed;
        addMileage(distance);
    }

    @Override
    public double calculateFuelEfficiency() { return 8.0; }

    @Override
    public void refuel(double amount) throws InvalidOperationException {
        if (amount <= 0) throw new InvalidOperationException("Refuel amount must be positive");
        fuelLevel += amount;
    }

    @Override
    public double getFuelLevel() { return fuelLevel; }

    @Override
    public double consumeFuel(double distance) throws InsufficientFuelException, InvalidOperationException {
        if (distance < 0) throw new InvalidOperationException("Distance cannot be negative");
        double eff = calculateFuelEfficiency();
        if (currentCargo > 0.5 * cargoCapacity) eff *= 0.9;
        double needed = distance / eff;
        if (needed > fuelLevel) throw new InsufficientFuelException("Not enough fuel in truck " + getId());
        fuelLevel -= needed;
        return needed;
    }

    @Override
    public void loadCargo(double weight) throws OverloadException, InvalidOperationException {
        if (weight <= 0) throw new InvalidOperationException("Weight must be positive");
        if (currentCargo + weight > cargoCapacity) throw new OverloadException("Truck over capacity");
        currentCargo += weight;
    }

    @Override
    public void unloadCargo(double weight) throws InvalidOperationException {
        if (weight <= 0 || weight > currentCargo) throw new InvalidOperationException("Invalid unload weight");
        currentCargo -= weight;
    }

    @Override
    public double getCargoCapacity() { return cargoCapacity; }
    @Override
    public double getCurrentCargo() { return currentCargo; }

    @Override
    public void scheduleMaintenance() { maintenanceNeeded = true; }
    @Override
    public boolean needsMaintenance() { return getCurrentMileage() > 10000 || maintenanceNeeded; }
    @Override
    public void performMaintenance() {
        maintenanceNeeded = false;
        System.out.println("Truck " + getId() + " maintenance performed.");
    }

    @Override
    public String toCSV() {
        return String.join(",",
                "Truck", getId(), getModel(), String.valueOf(getMaxSpeed()),
                String.valueOf(getCurrentMileage()), String.valueOf(getNumWheels()),
                String.valueOf(fuelLevel), String.valueOf(cargoCapacity),
                String.valueOf(currentCargo), String.valueOf(needsMaintenance()));
    }
}
