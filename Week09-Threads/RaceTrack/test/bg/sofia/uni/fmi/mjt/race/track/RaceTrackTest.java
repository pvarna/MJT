package bg.sofia.uni.fmi.mjt.race.track;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RaceTrackTest {
    @Test
    void mainTest() throws InterruptedException {
        Track track = new RaceTrack(1);

        Car car1 = new Car(37, 2, track);
        Car car2 = new Car(40, 2, track);
        Car car3 = new Car(45, 2, track);

        Thread thread1 = new Thread(car1);
        Thread thread2 = new Thread(car2);
        Thread thread3 = new Thread(car3);

        thread1.start();
        thread2.start();
        thread3.start();

        thread1.join();
        thread2.join();
        thread3.join();

        Thread.sleep(3000);

        track.getPit().finishRace();

        List<Integer> expected = List.of(37,40,45);
        assertTrue(expected.containsAll(track.getFinishedCarsIds()) && track.getFinishedCarsIds().containsAll(expected));
        assertEquals(6, track.getPit().getPitStopsCount());
        assertEquals(3, track.getNumberOfFinishedCars());
    }
}