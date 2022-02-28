package bg.sofia.uni.fmi.mjt.twitch.content.stream;

import bg.sofia.uni.fmi.mjt.twitch.content.Content;
import bg.sofia.uni.fmi.mjt.twitch.content.video.Video;

public interface Stream extends Content {
    void start();

    Video end();
}
