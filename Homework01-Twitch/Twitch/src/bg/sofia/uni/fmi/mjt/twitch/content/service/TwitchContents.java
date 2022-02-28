package bg.sofia.uni.fmi.mjt.twitch.content.service;

import bg.sofia.uni.fmi.mjt.twitch.content.Content;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class TwitchContents implements ContentService {
    private final List<Content> contents;

    public TwitchContents() {
        this.contents = new ArrayList<>();
    }

    @Override
    public void addContent(Content content) {
        this.contents.add(content);
    }

    @Override
    public void removeContent(Content content) {
        this.contents.remove(content);
    }

    @Override
    public Content getMostWatchedContent() {
        if (this.contents.isEmpty()) {
            return null;
        }

        this.contents.sort(new SortListByViews());

        Content result = this.contents.get(0);
        if (result.getNumberOfViews() == 0) {
            return null;
        }

        return result;
    }

    @Override
    public Content getMostWatchedContentFrom(String username) {
        if (this.contents.isEmpty()) {
            return null;
        }

        Content result = null;

        this.contents.sort(new SortListByViews());

        for (Content current : this.contents) {
            if (current.getMetadata().streamer().getName().equals(username)) {
                result = current;
                break;
            }
        }

        if (result == null || result.getNumberOfViews() == 0) {
            return null;
        }

        return result;
    }
}

class SortListByViews implements Comparator<Content> {
    @Override
    public int compare(Content o1, Content o2) {
        return o2.getNumberOfViews() - o1.getNumberOfViews();
    }
}
