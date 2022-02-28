package bg.sofia.uni.fmi.mjt.twitch.content.video;

import bg.sofia.uni.fmi.mjt.twitch.content.ContentAbstract;
import bg.sofia.uni.fmi.mjt.twitch.content.stream.Stream;
import bg.sofia.uni.fmi.mjt.twitch.user.User;

import java.time.Duration;

public class VideoClass extends ContentAbstract implements Video {
    private final Duration duration;

    public VideoClass(Stream stream, Duration duration) {
        super(stream.getMetadata().title(), stream.getMetadata().category(), stream.getMetadata().streamer());
        this.duration = duration;
    }

    @Override
    public void stopWatching(User user) {

    }

    @Override
    public Duration getDuration() {
        return this.duration;
    }
}
