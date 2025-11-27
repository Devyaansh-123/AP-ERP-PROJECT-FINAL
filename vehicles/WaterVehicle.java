package vehicles;

import exceptions.InvalidOperationException;

public abstract class WaterVehicle extends Vehicle {
    private final boolean hasSail;

    protected WaterVehicle(String id, String model, double maxSpeed, double currentMileage, boolean hasSail)
            throws InvalidOperationException {
        super(id, model, maxSpeed, currentMileage);
        this.hasSail = hasSail;
    }

    public boolean isHasSail() { return hasSail; }

    @Override
    public double estimateJourneyTime(double distance) throws InvalidOperationException {
        return super.estimateJourneyTime(distance) * 1.15;
    }
}
