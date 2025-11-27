package vehicles;

import exceptions.InsufficientFuelException;
import exceptions.InvalidOperationException;
import interfaces.FuelConsumable;
import interfaces.Maintainable;
import interfaces.PassengerCarrier;

public class Car extends LandVehicle implements FuelConsumable, PassengerCarrier, Maintainable {
    private double fuelLevel;
    private final int passengerCapacity = 5;
    private int currentPassengers = 0;
    private boolean maintenanceNeeded = false;

    public Car(String id, String model, double maxSpeed, double currentMileage, int numWheels)
            throws InvalidOperationException {
        super(id, model, maxSpeed, currentMileage, numWheels);
        this.fuelLevel = 0;
    }

    @Override
    public void move(double distance) throws InvalidOperationException, InsufficientFuelException {
        if (distance < 0) throw new InvalidOperationException("Distance cannot be negative");
        double needed = distance / calculateFuelEfficiency();
        if (needed > fuelLevel) throw new InsufficientFuelException("Not enough fuel in car " + getId());
        fuelLevel -= needed;
        addMileage(distance);
    }

    @Override
    public double calculateFuelEfficiency() { return 15.0; }

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
        if (needed > fuelLevel) throw new InsufficientFuelException("Not enough fuel in car " + getId());
        fuelLevel -= needed;
        return needed;
    }

    @Override
    public void boardPassengers(int count) throws InvalidOperationException {
        if (count <= 0) throw new InvalidOperationException("Passenger count must be positive");
        if (currentPassengers + count > passengerCapacity)
            throw new InvalidOperationException("Over capacity");
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
    public void scheduleMaintenance() { maintenanceNeeded = true; }
    @Override
    public boolean needsMaintenance() { return getCurrentMileage() > 10000 || maintenanceNeeded; }
    @Override
    public void performMaintenance() {
        maintenanceNeeded = false;
        System.out.println("Car " + getId() + " maintenance performed.");
    }

    @Override
    public String toCSV() {
        return String.join(",",
                "Car", getId(), getModel(), String.valueOf(getMaxSpeed()),
                String.valueOf(getCurrentMileage()), String.valueOf(getNumWheels()),
                String.valueOf(fuelLevel), String.valueOf(passengerCapacity),
                String.valueOf(currentPassengers), String.valueOf(needsMaintenance()));
    }
}
