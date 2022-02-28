package bg.sofia.uni.fmi.mjt.twitch.user.service;

import bg.sofia.uni.fmi.mjt.twitch.content.Category;
import bg.sofia.uni.fmi.mjt.twitch.content.Content;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TwitchUserContentsWatched implements UserContentsWatched {
    private final Map<String, List<Content>> userWatchesPerCategory;

    public TwitchUserContentsWatched() {
        this.userWatchesPerCategory = new HashMap<>();
    }

    @Override
    public List<Category> getMostWatchedCategoriesBy(String username) {
        Map<Category, Integer> viewsPerCategory = getViewsPerCategory(username);
        if (viewsPerCategory == null) {
            return List.copyOf(new ArrayList<>());
        }

        List<Map.Entry<Category, Integer>> list = new ArrayList<>(viewsPerCategory.entrySet());
        list.sort(new SortMapEntriesByValue());

        HashMap<Category, Integer> sortedWatchersPerCategory = new LinkedHashMap<>();
        for (Map.Entry<Category, Integer> current : list) {
            sortedWatchersPerCategory.put(current.getKey(), current.getValue());
        }

        return List.copyOf(new LinkedList<>(sortedWatchersPerCategory.keySet()));
    }

    @Override
    public void addWatch(String username, Content content) {
        if (!this.userWatchesPerCategory.containsKey(username)) {
            this.userWatchesPerCategory.put(username, new ArrayList<>());
        }
        this.userWatchesPerCategory.get(username).add(content);
    }

    private Map<Category, Integer> getViewsPerCategory(String username) {
        Map<Category, Integer> result = new HashMap<>();

        List<Content> watchedContent = this.userWatchesPerCategory.get(username);
        if (watchedContent == null) {
            return null;
        }

        for (Content currentContent : watchedContent) {
            Category currentCategory = currentContent.getMetadata().category();

            if (result.containsKey(currentCategory)) {
                result.put(currentCategory, result.get(currentCategory) + 1);
            } else {
                result.put(currentCategory, 1);
            }
        }

        return result;
    }
}

class SortMapEntriesByValue implements Comparator<Map.Entry<Category, Integer>> {
    public int compare(Map.Entry<Category, Integer> o1, Map.Entry<Category, Integer> o2) {
        return (o2.getValue()).compareTo(o1.getValue());
    }
}
