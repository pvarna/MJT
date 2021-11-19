package bg.sofia.uni.fmi.mjt.spotify.playable;

public abstract class PlayableAbstract implements Playable {
    private final String title;
    private final String artist;
    private final int year;
    private final double duration;
    private int numberOfTimesPlayed;

    public PlayableAbstract(String title, String artist, int year, double duration) {
        this.title = title;
        this.artist = artist;
        this.year = year;
        this.duration = duration;
        this.numberOfTimesPlayed = 0;
    }

    public void increaseTotalCount() {
        ++this.numberOfTimesPlayed;
    }

    @Override
    public int getTotalPlays() {
        return this.numberOfTimesPlayed;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public String getArtist() {
        return this.artist;
    }

    @Override
    public int getYear() {
        return this.year;
    }

    @Override
    public double getDuration() {
        return this.duration;
    }
}
