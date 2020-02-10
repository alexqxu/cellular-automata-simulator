package cellsociety.exceptions;

public class InvalidImageException extends RuntimeException {
    public InvalidImageException(Throwable cause) {
        super(cause);
    }

    public InvalidImageException(Throwable cause, String message) {
        super(message, cause);
    }
}
