package bg.sofia.uni.fmi.mjt.rentalservice.vehicle;

import bg.sofia.uni.fmi.mjt.rentalservice.location.Location;

import java.time.LocalDateTime;
import java.util.Objects;

public abstract class VehicleAbstract implements Vehicle {
    private final String id;
    private final Location location;
    private LocalDateTime endOfReservationPeriod;

    public VehicleAbstract(String id, Location location) {
        this.id = id;
        this.location = location;
        this.endOfReservationPeriod = LocalDateTime.now();
    }
    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public Location getLocation() {
        return this.location;
    }

    @Override
    public LocalDateTime getEndOfReservationPeriod() {
        return this.endOfReservationPeriod;
    }

    @Override
    public void setEndOfReservationPeriod(LocalDateTime until) {
        this.endOfReservationPeriod = until;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VehicleAbstract that = (VehicleAbstract) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
