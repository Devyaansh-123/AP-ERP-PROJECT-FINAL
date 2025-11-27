package vehicles;

import exceptions.InvalidOperationException;

public abstract class AirVehicle extends Vehicle {
    private final double maxAltitude;

    protected AirVehicle(String id, String model, double maxSpeed, double currentMileage, double maxAltitude)
            throws InvalidOperationException {
        super(id, model, maxSpeed, currentMileage);
        this.maxAltitude = maxAltitude;
    }

    public double getMaxAltitude() { return maxAltitude; }

    @Override
    public double estimateJourneyTime(double distance) throws InvalidOperationException {
        return super.estimateJourneyTime(distance) * 0.95;
    }
}
