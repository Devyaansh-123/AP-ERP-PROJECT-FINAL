package vehicles;

import exceptions.InsufficientFuelException;
import exceptions.InvalidOperationException;
import exceptions.OverloadException;
import interfaces.CargoCarrier;
import interfaces.FuelConsumable;
import interfaces.Maintainable;
import interfaces.PassengerCarrier;

public class Airplane extends AirVehicle implements FuelConsumable, PassengerCarrier, CargoCarrier, Maintainable {
    private double fuelLevel = 0;
    private final int passengerCapacity = 200;
    private int currentPassengers = 0;
    private final double cargoCapacity = 10000;
    private double currentCargo = 0;
    private boolean maintenanceNeeded = false;

    public Airplane(String id, String model, double maxSpeed, double currentMileage, double maxAltitude)
            throws InvalidOperationException {
        super(id, model, maxSpeed, currentMileage, maxAltitude);
    }

    @Override
    public void move(double distance) throws InvalidOperationException, InsufficientFuelException {
        if (distance < 0) throw new InvalidOperationException("Distance cannot be negative");
        double needed = distance / calculateFuelEfficiency();
        if (needed > fuelLevel) throw new InsufficientFuelException("Not enough fuel in airplane " + getId());
        fuelLevel -= needed;
        addMileage(distance);
    }

    @Override
    public double calculateFuelEfficiency() { return 5.0; }

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
        double needed = distance / calculateFuelEfficiency();
        if (needed > fuelLevel) throw new InsufficientFuelException("Not enough fuel in airplane " + getId());
        fuelLevel -= needed;
        return needed;
    }

    @Override
    public void boardPassengers(int count) throws OverloadException, InvalidOperationException {
        if (count <= 0) throw new InvalidOperationException("Passenger count must be positive");
        if (currentPassengers + count > passengerCapacity) throw new OverloadException("Airplane over capacity");
        currentPassengers += count;
    }

    @Override
    public void disembarkPassengers(int count) throws InvalidOperationException {
        if (count <= 0 || count > currentPassengers) throw new InvalidOperationException("Invalid disembark count");
        currentPassengers -= count;
    }

    @Override
    public int getPassengerCapacity() { return passengerCapacity; }
    @Override
    public int getCurrentPassengers() { return currentPassengers; }

    @Override
    public void loadCargo(double weight) throws OverloadException, InvalidOperationException {
        if (weight <= 0) throw new InvalidOperationException("Weight must be positive");
        if (currentCargo + weight > cargoCapacity) throw new OverloadException("Airplane cargo over capacity");
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
        System.out.println("Airplane " + getId() + " maintenance performed.");
    }

    @Override
    public String toCSV() {
        return String.join(",",
                "Airplane", getId(), getModel(), String.valueOf(getMaxSpeed()),
                String.valueOf(getCurrentMileage()), String.valueOf(getMaxAltitude()),
                String.valueOf(fuelLevel), String.valueOf(passengerCapacity),
                String.valueOf(currentPassengers), String.valueOf(cargoCapacity),
                String.valueOf(currentCargo), String.valueOf(needsMaintenance()));
    }
}
