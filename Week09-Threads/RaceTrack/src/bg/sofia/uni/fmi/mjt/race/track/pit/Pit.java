package bg.sofia.uni.fmi.mjt.race.track.pit;

import bg.sofia.uni.fmi.mjt.race.track.Car;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Pit {
    private final int nPitTeams;
    private List<PitTeam> pitTeams;
    private final Deque<Car> waitingCars;
    private final AtomicInteger pitStopsCount;
    private boolean isClosingTime;

    public Pit(int nPitTeams) {
        this.nPitTeams = nPitTeams;
        this.waitingCars = new ArrayDeque<>();
        this.startPitTeams();
        this.pitStopsCount = new AtomicInteger(0);
    }

    public void submitCar(Car car) {
        synchronized (this) {
            if (!isClosingTime) {
                waitingCars.add(car);
                pitStopsCount.incrementAndGet();
            }
            this.notifyAll();
        }
    }

    public synchronized Car getCar() {
        while (!isClosingTime && waitingCars.isEmpty()) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return this.waitingCars.poll();
    }

    public synchronized int getPitStopsCount() {
        return this.pitStopsCount.get();
    }

    public List<PitTeam> getPitTeams() {
        return List.copyOf(this.pitTeams);
    }

    public synchronized void finishRace() {
        this.isClosingTime = true;
        this.notifyAll();
    }

    private void startPitTeams() {
        this.pitTeams = new ArrayList<>();
        for (int i = 0; i < nPitTeams; ++i) {
            this.pitTeams.add(new PitTeam(i, this));
            this.pitTeams.get(i).start();
        }
    }
}