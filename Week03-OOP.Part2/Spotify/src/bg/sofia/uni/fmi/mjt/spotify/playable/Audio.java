package bg.sofia.uni.fmi.mjt.spotify.playable;

public class Audio extends PlayableAbstract {
    public Audio(String title, String artist, int year, double duration) {
        super(title, artist, year, duration);
    }

    @Override
    public String play() {
        increaseTotalCount();
        return "Currently playing audio content: " + this.getTitle();
    }
}
