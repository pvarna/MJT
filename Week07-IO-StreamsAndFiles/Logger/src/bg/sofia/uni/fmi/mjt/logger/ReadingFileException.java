package bg.sofia.uni.fmi.mjt.logger;

public class ReadingFileException extends RuntimeException {
    public ReadingFileException() {
    }

    public ReadingFileException(String message) {
        super(message);
    }

    public ReadingFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
