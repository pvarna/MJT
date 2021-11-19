package bg.sofia.uni.fmi.mjt.spotify.playable;

public class Video extends PlayableAbstract {
    public Video(String title, String artist, int year, double duration) {
        super(title, artist, year, duration);
    }

    @Override
    public String play() {
        increaseTotalCount();
        return "Currently playing video content: " + this.getTitle();
    }
}
