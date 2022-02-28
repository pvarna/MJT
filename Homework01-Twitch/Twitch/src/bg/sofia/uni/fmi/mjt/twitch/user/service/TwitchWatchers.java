package bg.sofia.uni.fmi.mjt.twitch.user.service;

import bg.sofia.uni.fmi.mjt.twitch.user.User;

import java.util.HashMap;
import java.util.Map;

public class TwitchWatchers implements UserWatchers {
    private final Map<User, Integer> users;

    public TwitchWatchers() {
        this.users = new HashMap<>();
    }

    @Override
    public void addWatcher(User user) {
        if (this.users.containsKey(user)) {
            this.users.put(user, this.users.get(user) + 1);
        } else {
            this.users.put(user, 1);
        }
    }

    @Override
    public void removeWatchers(User user, int watchers) {
        this.users.put(user, this.users.get(user) - watchers);
    }

    @Override
    public User getMostWatchedUser() {
        User result = null;
        int mostWatchers = 0;

        for (User current : this.users.keySet()) {
            int currentWatchers = this.users.get(current);

            if (result == null) {
                result = current;
                mostWatchers = currentWatchers;
            } else {
                if (currentWatchers > mostWatchers) {
                    result = current;
                    mostWatchers = currentWatchers;
                }
            }
        }

        if (result != null && mostWatchers == 0) {
            return null;
        }

        return result;
    }
}
