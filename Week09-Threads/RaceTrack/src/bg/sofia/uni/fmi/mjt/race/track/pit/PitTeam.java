package bg.sofia.uni.fmi.mjt.race.track.pit;

import bg.sofia.uni.fmi.mjt.race.track.Car;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class PitTeam extends Thread {
    private final int id;
    private final Pit pitStop;
    private final AtomicInteger counterPitStoppedCars;

    private static final Random MY_RANDOM = new Random();
    private static final int MAX_MILLISECONDS_PIT_TEAM = 201;

    public PitTeam(int id, Pit pitStop) {
        this.id = id;
        this.pitStop = pitStop;
        this.counterPitStoppedCars = new AtomicInteger(0);
    }

    @Override
    public void run() {
        Car toRepair = null;

        while ((toRepair = pitStop.getCar()) != null) {
            try {
                Thread.sleep(MY_RANDOM.nextInt(MAX_MILLISECONDS_PIT_TEAM));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            this.counterPitStoppedCars.incrementAndGet();

            Thread newCar = new Thread(new Car(toRepair.getCarId(), toRepair.getNPitStops() - 1, toRepair.getTrack()));
            newCar.start();
        }

    }

    public int getPitStoppedCars() {
        return this.counterPitStoppedCars.get();
    }

}