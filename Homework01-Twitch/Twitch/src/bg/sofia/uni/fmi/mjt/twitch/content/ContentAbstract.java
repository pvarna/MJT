package bg.sofia.uni.fmi.mjt.twitch.content;

import bg.sofia.uni.fmi.mjt.twitch.user.User;

import java.util.Objects;

public abstract class ContentAbstract implements Content {
    protected Metadata metadata;
    protected int numberOfViews;

    public ContentAbstract(String title, Category category, User streamer) {
        this.metadata = new Metadata(title, category, streamer);
    }

    @Override
    public Metadata getMetadata() {
        return this.metadata;
    }

    @Override
    public int getNumberOfViews() {
        return this.numberOfViews;
    }

    @Override
    public void startWatching(User user) {
        ++this.numberOfViews;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContentAbstract that = (ContentAbstract) o;
        return numberOfViews == that.numberOfViews && metadata.equals(that.metadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(metadata, numberOfViews);
    }
}
