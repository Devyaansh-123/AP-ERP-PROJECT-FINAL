package vehicles;

import exceptions.InvalidOperationException;

public abstract class LandVehicle extends Vehicle {
    private final int numWheels;

    protected LandVehicle(String id, String model, double maxSpeed, double currentMileage, int numWheels)
            throws InvalidOperationException {
        super(id, model, maxSpeed, currentMileage);
        this.numWheels = numWheels;
    }

    public int getNumWheels() { return numWheels; }

    @Override
    public double estimateJourneyTime(double distance) throws InvalidOperationException {
        return super.estimateJourneyTime(distance) * 1.10;
    }
}
