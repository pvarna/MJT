package bg.sofia.uni.fmi.mjt.twitch.user.service;

import bg.sofia.uni.fmi.mjt.twitch.content.Category;
import bg.sofia.uni.fmi.mjt.twitch.content.Content;

import java.util.List;

public interface UserContentsWatched {
    List<Category> getMostWatchedCategoriesBy(String username);

    void addWatch(String username, Content content);
}
