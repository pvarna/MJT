package bg.sofia.uni.fmi.mjt.news.dto;

import java.net.URL;
import java.util.Objects;

public class Article {

    private final Source source;
    private final String author;
    private final String title;
    private final String description;
    private final String url;
    private final String urlToImage;
    private final String publishedAt;
    private final String content;

    public Article(Source source, String author, String title, String description, String url, String urlToImage, String publishedAt, String content) {
        this.source = source;
        this.author = author;
        this.title = title;
        this.description = description;
        this.url = url;
        this.urlToImage = urlToImage;
        this.publishedAt = publishedAt;
        this.content = content;
    }

    // for test purposes
    @Override
    public String toString() {

        return  "Source: " + this.source.getName() + System.lineSeparator() +
                "Title: " + this.title + System.lineSeparator() +
                "Author: " + this.author + System.lineSeparator() +
                "Date: " + this.publishedAt + System.lineSeparator() +
                "Description: " + this.description + System.lineSeparator() +
                "Content: " + this.content + System.lineSeparator() +
                "Full article: " + this.url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Article article = (Article) o;
        return source.equals(article.source) && author.equals(article.author) && title.equals(article.title) && description.equals(article.description) && url.equals(article.url) && urlToImage.equals(article.urlToImage) && publishedAt.equals(article.publishedAt) && content.equals(article.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, author, title, description, url, urlToImage, publishedAt, content);
    }
}
