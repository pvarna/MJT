package bg.sofia.uni.fmi.mjt.news.exceptions;

public class BadAPIKeyException extends NewsFeedClientException {

    public BadAPIKeyException(String message) {
        super(message);
    }

    public BadAPIKeyException(String message, Throwable cause) {
        super(message, cause);
    }
}
