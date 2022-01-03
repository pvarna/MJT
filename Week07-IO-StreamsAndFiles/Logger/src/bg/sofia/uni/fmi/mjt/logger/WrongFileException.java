package bg.sofia.uni.fmi.mjt.logger;

public class WrongFileException extends RuntimeException {
    public WrongFileException() {
    }

    public WrongFileException(String message) {
        super(message);
    }

    public WrongFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
