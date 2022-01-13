package bg.sofia.uni.fmi.mjt.race.track;

import java.util.Random;

public class Car implements Runnable {
    private final int id;
    private final int nPitStops;
    private final Track track;

    private static final Random MY_RANDOM = new Random();
    private static final int MAX_MILLISECONDS_CAR = 1001;

    public Car(int id, int nPitStops, Track track) {
        this.id = id;
        this.nPitStops = nPitStops;
        this.track = track;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(MY_RANDOM.nextInt(MAX_MILLISECONDS_CAR));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        track.enterPit(this);
    }

    public int getCarId() {
        return this.id;
    }

    public int getNPitStops() {
        return this.nPitStops;
    }

    public Track getTrack() {
        return this.track;
    }

}