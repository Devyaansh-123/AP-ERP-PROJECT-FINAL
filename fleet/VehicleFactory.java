package fleet;

import exceptions.InvalidOperationException;
import vehicles.*;

public class VehicleFactory {

    // Factory method to recreate a Vehicle from CSV line
    public static Vehicle createFromCSV(String csv) throws Exception {
        String[] t = csv.split(",");
        String type = t[0];

        switch (type) {
            case "Car":
                return new Car(t[1], t[2],
                        Double.parseDouble(t[3]), Double.parseDouble(t[4]),
                        Integer.parseInt(t[5]));

            case "Truck":
                return new Truck(t[1], t[2],
                        Double.parseDouble(t[3]), Double.parseDouble(t[4]),
                        Integer.parseInt(t[5]));

            case "Bus":
                return new Bus(t[1], t[2],
                        Double.parseDouble(t[3]), Double.parseDouble(t[4]),
                        Integer.parseInt(t[5]));

            case "Airplane":
                return new Airplane(t[1], t[2],
                        Double.parseDouble(t[3]), Double.parseDouble(t[4]),
                        Double.parseDouble(t[5]));

            case "CargoShip":
                return new CargoShip(t[1], t[2],
                        Double.parseDouble(t[3]), Double.parseDouble(t[4]),
                        Boolean.parseBoolean(t[5]));

            default:
                throw new InvalidOperationException("Unknown vehicle type: " + type);
        }
    }
}
