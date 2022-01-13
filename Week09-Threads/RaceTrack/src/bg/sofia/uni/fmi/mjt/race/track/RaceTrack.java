package bg.sofia.uni.fmi.mjt.race.track;

import bg.sofia.uni.fmi.mjt.race.track.pit.Pit;

import java.util.ArrayList;
import java.util.List;

public class RaceTrack implements Track {
    private final Pit pit;
    private final List<Integer> finishedCars;

    public RaceTrack(int pitTeams) {
        this.pit = new Pit(pitTeams);
        this.finishedCars = new ArrayList<>();
    }

    @Override
    public void enterPit(Car car) {
        if (car.getNPitStops() == 0) {
            this.finishedCars.add(car.getCarId());
        } else {
            this.pit.submitCar(car);
        }
    }

    @Override
    public int getNumberOfFinishedCars() {
        return this.finishedCars.size();
    }

    @Override
    public List<Integer> getFinishedCarsIds() {
        return List.copyOf(this.finishedCars);
    }

    @Override
    public Pit getPit() {
        return this.pit;
    }
}
