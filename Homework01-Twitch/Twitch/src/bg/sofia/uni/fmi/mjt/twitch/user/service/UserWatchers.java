package bg.sofia.uni.fmi.mjt.twitch.user.service;

import bg.sofia.uni.fmi.mjt.twitch.user.User;

public interface UserWatchers {
    void addWatcher(User user);

    void removeWatchers(User user, int watchers);

    User getMostWatchedUser();
}
