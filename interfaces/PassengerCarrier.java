package interfaces;

import exceptions.InvalidOperationException;
import exceptions.OverloadException;

public interface PassengerCarrier {
    void boardPassengers(int count) throws OverloadException, InvalidOperationException;
    void disembarkPassengers(int count) throws InvalidOperationException;
    int getPassengerCapacity();
    int getCurrentPassengers();
}
