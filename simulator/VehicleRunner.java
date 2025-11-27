package simulator;

import exceptions.InsufficientFuelException;
import exceptions.InvalidOperationException;
import vehicles.Vehicle;
import interfaces.FuelConsumable;

public class VehicleRunner implements Runnable {
    private final Vehicle vehicle;
    private final HighwayCounter counter;
    private volatile boolean running = true;
    private volatile boolean paused = false;
    private volatile boolean outOfFuel = false;
    private int localDistance = 0;
    private final long tickMillis;
    private final int incrementsPerTick;

    public VehicleRunner(Vehicle vehicle, HighwayCounter counter, long tickMillis) {
        this.vehicle = vehicle;
        this.counter = counter;
        this.tickMillis = tickMillis;
        this.incrementsPerTick = 1;
    }

    public VehicleRunner(Vehicle vehicle, HighwayCounter counter, long tickMillis, int incrementsPerTick) {
        this.vehicle = vehicle;
        this.counter = counter;
        this.tickMillis = tickMillis;
        this.incrementsPerTick = Math.max(1, incrementsPerTick);
    }

    @Override
    public void run() {
        while (running) {
            if (paused || outOfFuel) {
                sleep(100);
                continue;
            }
            for (int i = 0; i < incrementsPerTick; i++) {
                try {
                    vehicle.move(1);
                    counter.increment();
                    localDistance++;
                } catch (InsufficientFuelException e) {
                    outOfFuel = true;
                    break;
                } catch (InvalidOperationException e) {
                } catch (Exception e) {
                }
            }
            sleep(tickMillis);
        }
    }

    public void stop() { running = false; }
    public void pause() { paused = true; }
    public void resume() { paused = false; }
    public int getLocalDistance() { return localDistance; }
    public boolean isOutOfFuel() { return outOfFuel; }
    public double getMileage() { return vehicle.getCurrentMileage(); }
    public Vehicle getVehicle() { return vehicle; }

    public void refuel(double amount) {
        if (vehicle instanceof FuelConsumable fc) {
            try {
                fc.refuel(amount);
                outOfFuel = false;
            } catch (InvalidOperationException ignored) {
            }
        }
    }

    public double getFuelLevel() {
        if (vehicle instanceof FuelConsumable fc) {
            return fc.getFuelLevel();
        }
        return 0.0;
    }

    private void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }
}