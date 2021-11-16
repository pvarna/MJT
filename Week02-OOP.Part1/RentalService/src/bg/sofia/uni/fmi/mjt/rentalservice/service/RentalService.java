package bg.sofia.uni.fmi.mjt.rentalservice.service;

import bg.sofia.uni.fmi.mjt.rentalservice.location.Location;
import bg.sofia.uni.fmi.mjt.rentalservice.vehicle.Vehicle;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class RentalService implements RentalServiceAPI {
    private final Vehicle[] vehicles;

    public RentalService(Vehicle[] vehicles) {
        this.vehicles = new Vehicle[vehicles.length];
        System.arraycopy(vehicles, 0, this.vehicles, 0, vehicles.length);
    }

    @Override
    public double rentUntil(Vehicle vehicle, LocalDateTime until) {
        if (vehicle == null || until == null || until.isBefore(LocalDateTime.now())) {
            return -1.0;
        }

        int searchedIndex = -1;
        int size = this.vehicles.length;

        for (int i = 0; i < size; ++i)
        {
            if (vehicle.equals(this.vehicles[i])) {
                searchedIndex = i;
                break;
            }
        }

        if (searchedIndex == -1 || this.vehicles[searchedIndex].getEndOfReservationPeriod().isAfter(LocalDateTime.now())) {
            return -1.0;
        }

        this.vehicles[searchedIndex].setEndOfReservationPeriod(until);

        long differenceMinutes = ChronoUnit.MINUTES.between(LocalDateTime.now(),
                this.vehicles[searchedIndex].getEndOfReservationPeriod()) + 1;

        return differenceMinutes * this.vehicles[searchedIndex].getPricePerMinute();
    }

    @Override
    public Vehicle findNearestAvailableVehicleInRadius(String type, Location location, double maxDistance) {
        if (type == null || location == null || maxDistance < 0.0) {
            return null;
        }

        double closestDistance = maxDistance;
        int bestIndex = -1;
        int size = this.vehicles.length;

        for (int i = 0; i < size; ++i) {
            double currentDistance = Location.calculateDistance(this.vehicles[i].getLocation(), location);

            if (type.equals(this.vehicles[i].getType()) && currentDistance <= closestDistance
                    && !(this.vehicles[i].getEndOfReservationPeriod().isAfter(LocalDateTime.now()))) {
                closestDistance = currentDistance;
                bestIndex = i;
            }
        }

        return (bestIndex == -1) ? null : this.vehicles[bestIndex];
    }
}
