package bg.sofia.uni.fmi.mjt.twitch.content.stream;

import bg.sofia.uni.fmi.mjt.twitch.content.Category;
import bg.sofia.uni.fmi.mjt.twitch.content.ContentAbstract;
import bg.sofia.uni.fmi.mjt.twitch.content.video.Video;
import bg.sofia.uni.fmi.mjt.twitch.content.video.VideoClass;
import bg.sofia.uni.fmi.mjt.twitch.user.User;
import bg.sofia.uni.fmi.mjt.twitch.user.UserStatus;

import java.time.Duration;
import java.time.LocalDateTime;

public class StreamClass extends ContentAbstract implements Stream {
    private final LocalDateTime start;

    public StreamClass(String title, Category category, User streamer) {
        super(title, category, streamer);
        this.start = LocalDateTime.now();
    }

    @Override
    public Duration getDuration() {
        return Duration.between(start, LocalDateTime.now());
    }

    @Override
    public void start() {
        this.metadata.streamer().setStatus(UserStatus.STREAMING);
    }

    @Override
    public Video end() {
        this.metadata.streamer().setStatus(UserStatus.OFFLINE);
        return new VideoClass(this, Duration.between(this.start, LocalDateTime.now()));
    }

    @Override
    public void stopWatching(User user) {
        --this.numberOfViews;
    }
}
