package bg.sofia.uni.fmi.mjt.twitch;

import bg.sofia.uni.fmi.mjt.twitch.content.Category;
import bg.sofia.uni.fmi.mjt.twitch.content.Content;
import bg.sofia.uni.fmi.mjt.twitch.content.service.ContentService;
import bg.sofia.uni.fmi.mjt.twitch.content.service.TwitchContents;
import bg.sofia.uni.fmi.mjt.twitch.content.stream.Stream;
import bg.sofia.uni.fmi.mjt.twitch.content.stream.StreamClass;
import bg.sofia.uni.fmi.mjt.twitch.content.video.Video;
import bg.sofia.uni.fmi.mjt.twitch.user.User;
import bg.sofia.uni.fmi.mjt.twitch.user.UserNotFoundException;
import bg.sofia.uni.fmi.mjt.twitch.user.UserStatus;
import bg.sofia.uni.fmi.mjt.twitch.user.UserStreamingException;
import bg.sofia.uni.fmi.mjt.twitch.user.service.*;

import java.util.List;

public class Twitch implements StreamingPlatform {
    private final UserService userService;
    private final ContentService contentService;
    private final UserWatchers userWatchers;
    private final UserContentsWatched userContentsWatched;

    public Twitch(UserService userService) {
        this.userService = userService;
        this.contentService = new TwitchContents();
        this.userWatchers = new TwitchWatchers();
        this.userContentsWatched = new TwitchUserContentsWatched();
    }

    @Override
    public Stream startStream(String username, String title, Category category)
                                throws UserNotFoundException, UserStreamingException {
        if (username == null || username.isEmpty() || title == null || title.isEmpty() || category == null) {
            throw new IllegalArgumentException();
        }

        if (!this.userService.getUsers().containsKey(username)) {
            throw new UserNotFoundException();
        }

        User user = this.userService.getUsers().get(username);
        if (user.getStatus() == UserStatus.STREAMING) {
            throw new UserStreamingException(user.getStatus().getMessage());
        }

        Stream newStream = new StreamClass(title, category, user);
        newStream.start();

        this.contentService.addContent(newStream);

        return newStream;
    }

    @Override
    public Video endStream(String username, Stream stream) throws UserNotFoundException, UserStreamingException {
        if (username == null || username.isEmpty() || stream == null) {
            throw new IllegalArgumentException();
        }

        if (!this.userService.getUsers().containsKey(username)) {
            throw new UserNotFoundException();
        }

        User user = this.userService.getUsers().get(username);
        if (user.getStatus() == UserStatus.OFFLINE) {
            throw new UserStreamingException(user.getStatus().getMessage());
        }

        Video newVideo = stream.end();

        this.contentService.removeContent(stream);
        if (stream.getNumberOfViews() > 0) {
            this.userWatchers.removeWatchers(stream.getMetadata().streamer(), stream.getNumberOfViews());
        }
        this.contentService.addContent(newVideo);

        return newVideo;
    }

    @Override
    public void watch(String username, Content content) throws UserNotFoundException, UserStreamingException {
        if (username == null || username.isEmpty() || content == null) {
            throw new IllegalArgumentException();
        }

        if (!this.userService.getUsers().containsKey(username)) {
            throw new UserNotFoundException();
        }

        User user = this.userService.getUsers().get(username);
        if (user.getStatus() == UserStatus.STREAMING) {
            throw new UserStreamingException(user.getStatus().getMessage());
        }

        content.startWatching(user);
        this.userWatchers.addWatcher(content.getMetadata().streamer());
        this.userContentsWatched.addWatch(username, content);
    }

    @Override
    public User getMostWatchedStreamer() {
        return this.userWatchers.getMostWatchedUser();
    }

    @Override
    public Content getMostWatchedContent() {
        return this.contentService.getMostWatchedContent();
    }

    @Override
    public Content getMostWatchedContentFrom(String username) throws UserNotFoundException {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException();
        }

        if (!this.userService.getUsers().containsKey(username)) {
            throw new UserNotFoundException();
        }

        return this.contentService.getMostWatchedContentFrom(username);
    }

    @Override
    public List<Category> getMostWatchedCategoriesBy(String username) throws UserNotFoundException {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException();
        }

        if (!this.userService.getUsers().containsKey(username)) {
            throw new UserNotFoundException();
        }

        return List.copyOf(this.userContentsWatched.getMostWatchedCategoriesBy(username));
    }
}
