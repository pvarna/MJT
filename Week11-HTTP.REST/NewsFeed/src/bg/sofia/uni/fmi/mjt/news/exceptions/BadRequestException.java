package bg.sofia.uni.fmi.mjt.news.exceptions;

public class BadRequestException extends  NewsFeedClientException {

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
