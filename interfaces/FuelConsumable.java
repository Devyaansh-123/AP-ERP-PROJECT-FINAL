package interfaces;

import exceptions.InvalidOperationException;
import exceptions.InsufficientFuelException;

public interface FuelConsumable {
    void refuel(double amount) throws InvalidOperationException;
    double getFuelLevel();
    double consumeFuel(double distance) throws InsufficientFuelException, InvalidOperationException;
}
