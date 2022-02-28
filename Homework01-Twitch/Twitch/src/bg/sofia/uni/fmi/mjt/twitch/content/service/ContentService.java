package bg.sofia.uni.fmi.mjt.twitch.content.service;

import bg.sofia.uni.fmi.mjt.twitch.content.Content;

public interface ContentService {
    void addContent(Content content);

    void removeContent(Content content);

    Content getMostWatchedContent();

    Content getMostWatchedContentFrom(String username);
}
