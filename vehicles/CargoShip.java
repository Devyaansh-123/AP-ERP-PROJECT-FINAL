package vehicles;

import exceptions.InvalidOperationException;
import exceptions.OverloadException;
import interfaces.CargoCarrier;
import interfaces.Maintainable;

import interfaces.FuelConsumable;

public class CargoShip extends WaterVehicle implements CargoCarrier, Maintainable, FuelConsumable {
    private final double cargoCapacity = 50000;
    private double currentCargo = 0;
    private boolean maintenanceNeeded = false;
    private double fuelLevel = 0;

    public CargoShip(String id, String model, double maxSpeed, double currentMileage, boolean hasSail)
            throws InvalidOperationException {
        super(id, model, maxSpeed, currentMileage, hasSail);
    }

    @Override
    public void move(double distance) throws InvalidOperationException {
        if (distance < 0) throw new InvalidOperationException("Distance cannot be negative");
        
        // Consume fuel if not sailing
        if (!isHasSail()) {
             try {
                 consumeFuel(distance);
             } catch (exceptions.InsufficientFuelException e) {
                 System.out.println("CargoShip " + getId() + " out of fuel!");
                 return; // Stop moving
             }
        }
        
        addMileage(distance);
    }

    @Override
    public double calculateFuelEfficiency() {
        return isHasSail() ? 0.0 : 4.0;
    }

    @Override
    public void refuel(double amount) throws InvalidOperationException {
        if (amount <= 0) throw new InvalidOperationException("Refuel amount must be positive");
        fuelLevel += amount;
    }

    @Override
    public double getFuelLevel() {
        return fuelLevel;
    }

    @Override
    public double consumeFuel(double distance) throws exceptions.InsufficientFuelException, InvalidOperationException {
        if (distance < 0) throw new InvalidOperationException("Distance cannot be negative");
        if (isHasSail()) return 0; // Sails don't consume fuel
        
        double needed = distance / calculateFuelEfficiency();
        if (needed > fuelLevel) throw new exceptions.InsufficientFuelException("Not enough fuel in CargoShip " + getId());
        fuelLevel -= needed;
        return needed;
    }

    @Override
    public void loadCargo(double weight) throws OverloadException, InvalidOperationException {
        if (weight <= 0) throw new InvalidOperationException("Weight must be positive");
        if (currentCargo + weight > cargoCapacity) throw new OverloadException("Ship cargo over capacity");
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
        System.out.println("CargoShip " + getId() + " maintenance performed.");
    }

    @Override
    public String toCSV() {
        return String.join(",",
                "CargoShip", getId(), getModel(), String.valueOf(getMaxSpeed()),
                String.valueOf(getCurrentMileage()), String.valueOf(isHasSail()),
                String.valueOf(cargoCapacity), String.valueOf(currentCargo),
                String.valueOf(needsMaintenance()), String.valueOf(fuelLevel));
    }
}
